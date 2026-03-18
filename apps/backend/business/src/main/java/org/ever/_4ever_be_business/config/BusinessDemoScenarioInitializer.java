package org.ever._4ever_be_business.config;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.EmployeeTraining;
import org.ever._4ever_be_business.hr.entity.LeaveRequest;
import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.enums.LeaveType;
import org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeTrainingRepository;
import org.ever._4ever_be_business.hr.repository.LeaveRequestRepository;
import org.ever._4ever_be_business.hr.repository.TrainingRepository;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.entity.OrderStatus;
import org.ever._4ever_be_business.order.entity.Quotation;
import org.ever._4ever_be_business.order.entity.QuotationApproval;
import org.ever._4ever_be_business.order.entity.QuotationItem;
import org.ever._4ever_be_business.order.entity.OrderItem;
import org.ever._4ever_be_business.order.enums.ApprovalStatus;
import org.ever._4ever_be_business.order.enums.Unit;
import org.ever._4ever_be_business.order.repository.OrderItemRepository;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever._4ever_be_business.order.repository.QuotationApprovalRepository;
import org.ever._4ever_be_business.order.repository.QuotationItemRepository;
import org.ever._4ever_be_business.order.repository.QuotationRepository;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.enums.AttendanceStatus;
import org.ever._4ever_be_business.tam.repository.AttendanceRepository;
import org.ever._4ever_be_business.voucher.entity.PurchaseVoucher;
import org.ever._4ever_be_business.voucher.entity.SalesVoucher;
import org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus;
import org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus;
import org.ever._4ever_be_business.voucher.repository.PurchaseVoucherRepository;
import org.ever._4ever_be_business.voucher.repository.SalesVoucherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@org.springframework.core.annotation.Order(90)
public class BusinessDemoScenarioInitializer implements CommandLineRunner {

    private static final String APPROVER_EMPLOYEE_ID = "019a3dec-a3f3-781c-986b-8c0368cb1e73";
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BusinessDemoScenarioInitializer.class);

    private final CustomerUserRepository customerUserRepository;
    private final EmployeeRepository employeeRepository;
    private final QuotationApprovalRepository quotationApprovalRepository;
    private final QuotationRepository quotationRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SalesVoucherRepository salesVoucherRepository;
    private final PurchaseVoucherRepository purchaseVoucherRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final TrainingRepository trainingRepository;
    private final EmployeeTrainingRepository employeeTrainingRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<CustomerUser> customerUsers = customerUserRepository.findAll().stream()
            .filter(user -> user.getCustomerCompany() != null)
            .sorted(Comparator.comparing(CustomerUser::getCustomerUserCode))
            .limit(8)
            .toList();
        List<Employee> employees = employeeRepository.findAll().stream()
            .sorted(Comparator.comparing(emp -> emp.getInternelUser().getEmployeeCode()))
            .toList();

        if (customerUsers.size() < 6 || employees.size() < 12) {
            log.warn("[BusinessDemoScenarioInitializer] 선행 고객/직원 데이터가 부족해 데모 시드를 건너뜁니다.");
            return;
        }

        upsertQuotations(customerUsers);
        upsertOrders(customerUsers);
        upsertVouchers(customerUsers);
        upsertAttendances(employees.subList(0, 12));
        upsertLeaveRequests(employees.subList(0, 10));
        upsertEmployeeTrainings(employees.subList(0, 12));
    }

    private void upsertQuotations(List<CustomerUser> customerUsers) throws Exception {
        Map<String, Quotation> existing = quotationRepository.findAll().stream()
            .collect(Collectors.toMap(Quotation::getQuotationCode, Function.identity(), (left, right) -> left));

        for (QuotationSeed seed : quotationSeeds(customerUsers)) {
            if (existing.containsKey(seed.code())) {
                continue;
            }

            QuotationApproval approval = new QuotationApproval(seed.approvalStatus(), seed.approvedAt(), seed.approvedByEmployeeId(), seed.rejectedReason());
            setPrivateField(approval, "id", seed.approvalId());
            quotationApprovalRepository.save(approval);
            touch("quotation_approval", seed.approvalId(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));

            Quotation quotation = new Quotation(
                seed.code(),
                seed.customerUser().getId(),
                seed.totalPrice(),
                approval,
                seed.dueDate(),
                seed.note()
            );
            quotation.uncheck();
            setPrivateField(quotation, "id", seed.id());
            quotationRepository.save(quotation);
            touch("quotation", seed.id(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));

            for (QuotationItemSeed itemSeed : seed.items()) {
                QuotationItem item = new QuotationItem(quotation, itemSeed.productId(), itemSeed.count(), Unit.EA, itemSeed.price());
                setPrivateField(item, "id", itemSeed.id());
                quotationItemRepository.save(item);
                touch("quotation_item", itemSeed.id(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));
            }
        }
    }

    private void upsertOrders(List<CustomerUser> customerUsers) throws Exception {
        Map<String, Quotation> quotationById = quotationRepository.findAll().stream()
            .collect(Collectors.toMap(Quotation::getId, Function.identity(), (left, right) -> left));
        Set<String> existingCodes = orderRepository.findAll().stream()
            .map(Order::getOrderCode)
            .collect(Collectors.toSet());

        for (OrderSeed seed : orderSeeds(customerUsers)) {
            if (existingCodes.contains(seed.code())) {
                continue;
            }

            Order order = new Order();
            order.setId(seed.id());
            order.setOrderCode(seed.code());
            order.setQuotation(seed.quotationId() == null ? null : quotationById.get(seed.quotationId()));
            order.setCustomerUserId(seed.customerUser().getId());
            order.setTotalPrice(seed.totalPrice());
            order.setOrderDate(seed.orderDate());
            order.setDueDate(seed.dueDate());
            order.setStatus(seed.status());
            orderRepository.save(order);
            touch("orders", seed.id(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));

            for (OrderItemSeed itemSeed : seed.items()) {
                OrderItem item = new OrderItem(order, itemSeed.productId(), itemSeed.count(), Unit.EA, itemSeed.price());
                setPrivateField(item, "id", itemSeed.id());
                orderItemRepository.save(item);
                touch("order_item", itemSeed.id(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));
            }
        }
    }

    private void upsertVouchers(List<CustomerUser> customerUsers) throws Exception {
        Map<String, Order> orderByCode = orderRepository.findAll().stream()
            .collect(Collectors.toMap(Order::getOrderCode, Function.identity(), (left, right) -> left));

        Set<String> existingSales = salesVoucherRepository.findAll().stream()
            .map(SalesVoucher::getVoucherCode)
            .collect(Collectors.toSet());
        for (SalesVoucherSeed seed : salesVoucherSeeds(customerUsers)) {
            if (existingSales.contains(seed.code())) {
                continue;
            }
            SalesVoucher voucher = new SalesVoucher(
                seed.customerCompany(),
                orderByCode.get(seed.orderCode()),
                seed.code(),
                seed.issueDate(),
                seed.dueDate(),
                seed.totalAmount(),
                seed.status(),
                seed.memo()
            );
            setPrivateField(voucher, "id", seed.id());
            salesVoucherRepository.save(voucher);
            touch("sales_voucher", seed.id(), seed.issueDate(), seed.issueDate().plusDays(1));
        }

        Set<String> existingPurchase = purchaseVoucherRepository.findAll().stream()
            .map(PurchaseVoucher::getVoucherCode)
            .collect(Collectors.toSet());
        for (PurchaseVoucherSeed seed : purchaseVoucherSeeds()) {
            if (existingPurchase.contains(seed.code())) {
                continue;
            }
            purchaseVoucherRepository.save(PurchaseVoucher.builder()
                .id(seed.id())
                .supplierCompanyId(seed.supplierCompanyId())
                .productOrderId(seed.productOrderId())
                .voucherCode(seed.code())
                .issueDate(seed.issueDate())
                .dueDate(seed.dueDate())
                .totalAmount(seed.totalAmount())
                .status(seed.status())
                .memo(seed.memo())
                .build());
            touch("purchase_voucher", seed.id(), seed.issueDate(), seed.issueDate().plusDays(1));
        }
    }

    private void upsertAttendances(List<Employee> employees) throws Exception {
        List<LocalDate> workDates = recentBusinessDays(14);
        for (int employeeIndex = 0; employeeIndex < employees.size(); employeeIndex++) {
            Employee employee = employees.get(employeeIndex);
            for (int dayIndex = 0; dayIndex < workDates.size(); dayIndex++) {
                LocalDate date = workDates.get(dayIndex);
                String attendanceId = String.format("att-%02d-%s", employeeIndex + 1, date.toString().replace("-", ""));
                if (attendanceRepository.existsById(attendanceId)) {
                    continue;
                }

                boolean late = employeeIndex % 5 == 0 && dayIndex % 4 == 0;
                boolean overtime = (employeeIndex + dayIndex) % 7 == 0;
                LocalDateTime checkIn = date.atTime(late ? 9 : 8, late ? 22 : 48);
                LocalDateTime checkOut = date.atTime(overtime ? 19 : 17, overtime ? 35 : 55);
                long workMinutes = java.time.Duration.between(checkIn, checkOut).toMinutes();
                Attendance attendance = new Attendance(
                    workMinutes,
                    date.atTime(9, 0),
                    late ? AttendanceStatus.LATE : AttendanceStatus.NORMAL,
                    checkIn,
                    checkOut,
                    Math.max(0L, workMinutes - 480),
                    employee
                );
                setPrivateField(attendance, "id", attendanceId);
                attendanceRepository.save(attendance);
                touch("attendance", attendanceId, date.atTime(9, 0), date.atTime(18, 0));
            }
        }
    }

    private void upsertLeaveRequests(List<Employee> employees) throws Exception {
        for (LeaveSeed seed : leaveSeeds(employees)) {
            if (leaveRequestRepository.existsById(seed.id())) {
                continue;
            }
            LeaveRequest request = new LeaveRequest(
                seed.employee(),
                seed.leaveType(),
                seed.startDate(),
                seed.endDate(),
                seed.leaveDays(),
                seed.reason()
            );
            if (seed.status() == org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.APPROVED) {
                request.approve();
            } else if (seed.status() == org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.REJECTED) {
                request.reject();
            }
            setPrivateField(request, "id", seed.id());
            leaveRequestRepository.save(request);
            touch("leave_request", seed.id(), seed.createdAt(), seed.createdAt().plusDays(1));
        }
    }

    private void upsertEmployeeTrainings(List<Employee> employees) throws Exception {
        Map<String, Training> trainingByName = trainingRepository.findAll().stream()
            .collect(Collectors.toMap(Training::getTrainingName, Function.identity(), (left, right) -> left));
        for (EmployeeTrainingSeed seed : employeeTrainingSeeds(employees, trainingByName)) {
            if (seed.training() == null) {
                continue;
            }
            boolean exists = employeeTrainingRepository.findByEmployeeId(seed.employee().getId()).stream()
                .anyMatch(history -> history.getTraining().getId().equals(seed.training().getId()));
            if (exists) {
                continue;
            }
            EmployeeTraining training = new EmployeeTraining(seed.employee(), seed.training(), seed.status());
            setPrivateField(training, "id", seed.id());
            employeeTrainingRepository.save(training);
            touch("employee_training", seed.id(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));
        }
    }

    private void touch(String table, String id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        jdbcTemplate.update(
            "UPDATE " + table + " SET created_at = ?, updated_at = ? WHERE id = ?",
            Timestamp.valueOf(createdAt),
            Timestamp.valueOf(updatedAt),
            id
        );
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new IllegalArgumentException("필드를 찾을 수 없습니다: " + fieldName);
    }

    private static LocalDateTime daysAgo(int daysAgo) {
        return LocalDateTime.now().minusDays(daysAgo).withHour(9).withMinute(0).withSecond(0).withNano(0);
    }

    private static BigDecimal amount(String value) {
        return new BigDecimal(value);
    }

    private List<LocalDate> recentBusinessDays(int count) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate cursor = LocalDate.now().minusDays(20);
        while (dates.size() < count) {
            if (cursor.getDayOfWeek() != DayOfWeek.SATURDAY && cursor.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dates.add(cursor);
            }
            cursor = cursor.plusDays(1);
        }
        return dates;
    }

    private List<QuotationSeed> quotationSeeds(List<CustomerUser> users) {
        return List.of(
            new QuotationSeed("qt-demo-001", "qa-demo-001", "QO-202512-001", users.get(0), amount("24800000"), ApprovalStatus.PENDING, null, null, daysAgo(70).plusDays(7), 70, "전방 범퍼 커버 연간 단가 견적",
                List.of(new QuotationItemSeed("qti-demo-001", "prd-demo-fg-001", 40L, amount("620000")))),
            new QuotationSeed("qt-demo-002", "qa-demo-002", "QO-202601-002", users.get(1), amount("17280000"), ApprovalStatus.PENDING, null, null, daysAgo(56).plusDays(8), 56, "라디에이터 그릴 보충 생산 견적",
                List.of(new QuotationItemSeed("qti-demo-002", "prd-demo-fg-002", 32L, amount("540000")))),
            new QuotationSeed("qt-demo-003", "qa-demo-003", "QO-202601-003", users.get(2), amount("24800000"), ApprovalStatus.APPROVAL, APPROVER_EMPLOYEE_ID, null, daysAgo(49).plusDays(6), 49, "범퍼 커버 양산 승인 대기 견적",
                List.of(new QuotationItemSeed("qti-demo-003", "prd-demo-fg-001", 40L, amount("620000")))),
            new QuotationSeed("qt-demo-004", "qa-demo-004", "QO-202602-004", users.get(3), amount("17280000"), ApprovalStatus.APPROVAL, APPROVER_EMPLOYEE_ID, null, daysAgo(35).plusDays(6), 35, "라디에이터 그릴 추가 수주 견적",
                List.of(new QuotationItemSeed("qti-demo-004", "prd-demo-fg-002", 32L, amount("540000")))),
            new QuotationSeed("qt-demo-005", "qa-demo-005", "QO-202602-005", users.get(4), amount("14950000"), ApprovalStatus.APPROVAL, APPROVER_EMPLOYEE_ID, null, daysAgo(21).plusDays(5), 21, "도어 트림 어셈블리 긴급 공급 견적",
                List.of(new QuotationItemSeed("qti-demo-005", "prd-demo-fg-003", 26L, amount("575000")))),
            new QuotationSeed("qt-demo-006", "qa-demo-006", "QO-202603-006", users.get(5), amount("4980000"), ApprovalStatus.REJECTED, APPROVER_EMPLOYEE_ID, "납기 미스매치", daysAgo(15).plusDays(4), 15, "배터리 브래킷 교체 수요 견적",
                List.of(new QuotationItemSeed("qti-demo-006", "prd-demo-fg-006", 10L, amount("498000")))),
            new QuotationSeed("qt-demo-007", "qa-demo-007", "QO-202603-007", users.get(6), amount("11700000"), ApprovalStatus.READY_FOR_SHIPMENT, APPROVER_EMPLOYEE_ID, null, daysAgo(8).plusDays(4), 8, "램프 하우징 출하 준비 견적",
                List.of(new QuotationItemSeed("qti-demo-007", "prd-demo-fg-004", 18L, amount("650000")))),
            new QuotationSeed("qt-demo-008", "qa-demo-008", "QO-202603-008", users.get(7), amount("7440000"), ApprovalStatus.READY_FOR_SHIPMENT, APPROVER_EMPLOYEE_ID, null, daysAgo(4).plusDays(4), 4, "휠 아치 라이너 주간 공급 견적",
                List.of(new QuotationItemSeed("qti-demo-008", "prd-demo-fg-005", 16L, amount("465000"))))
        );
    }

    private List<OrderSeed> orderSeeds(List<CustomerUser> users) {
        return List.of(
            new OrderSeed("ord-demo-001", "qt-demo-003", "OR-202601-001", users.get(2), amount("24800000"), daysAgo(45), daysAgo(35), OrderStatus.MATERIAL_PREPARATION, 45,
                List.of(new OrderItemSeed("oi-demo-001", "prd-demo-fg-001", 40L, 620000L))),
            new OrderSeed("ord-demo-002", "qt-demo-004", "OR-202601-002", users.get(3), amount("17280000"), daysAgo(31), daysAgo(22), OrderStatus.IN_PRODUCTION, 31,
                List.of(new OrderItemSeed("oi-demo-002", "prd-demo-fg-002", 32L, 540000L))),
            new OrderSeed("ord-demo-003", "qt-demo-005", "OR-202602-003", users.get(4), amount("14950000"), daysAgo(19), daysAgo(11), OrderStatus.MATERIAL_PREPARATION, 19,
                List.of(new OrderItemSeed("oi-demo-003", "prd-demo-fg-003", 26L, 575000L))),
            new OrderSeed("ord-demo-004", "qt-demo-007", "OR-202603-004", users.get(6), amount("11700000"), daysAgo(8), daysAgo(3), OrderStatus.IN_PRODUCTION, 8,
                List.of(new OrderItemSeed("oi-demo-004", "prd-demo-fg-004", 18L, 650000L))),
            new OrderSeed("ord-demo-005", "qt-demo-008", "OR-202603-005", users.get(7), amount("7440000"), daysAgo(4), daysAgo(1), OrderStatus.READY_FOR_SHIPMENT, 4,
                List.of(new OrderItemSeed("oi-demo-005", "prd-demo-fg-005", 16L, 465000L))),
            new OrderSeed("ord-demo-006", null, "OR-202603-006", users.get(5), amount("3984000"), daysAgo(2), daysAgo(6), OrderStatus.DELIVERED, 2,
                List.of(new OrderItemSeed("oi-demo-006", "prd-demo-fg-006", 8L, 498000L)))
        );
    }

    private List<SalesVoucherSeed> salesVoucherSeeds(List<CustomerUser> users) {
        return List.of(
            new SalesVoucherSeed("sv-demo-001", "SV-202512-001", users.get(2).getCustomerCompany(), "OR-202601-001", daysAgo(44), daysAgo(30), amount("24800000"), SalesVoucherStatus.PENDING, "범퍼 커버 1차 매출 전표"),
            new SalesVoucherSeed("sv-demo-002", "SV-202601-002", users.get(3).getCustomerCompany(), "OR-202601-002", daysAgo(30), daysAgo(18), amount("17280000"), SalesVoucherStatus.UNPAID, "라디에이터 그릴 보충 매출 전표"),
            new SalesVoucherSeed("sv-demo-003", "SV-202602-003", users.get(4).getCustomerCompany(), "OR-202602-003", daysAgo(18), daysAgo(7), amount("14950000"), SalesVoucherStatus.PAID, "도어 트림 긴급 매출 전표"),
            new SalesVoucherSeed("sv-demo-004", "SV-202603-004", users.get(6).getCustomerCompany(), "OR-202603-004", daysAgo(8), daysAgo(2), amount("11700000"), SalesVoucherStatus.PENDING, "램프 하우징 출하 전표"),
            new SalesVoucherSeed("sv-demo-005", "SV-202603-005", users.get(7).getCustomerCompany(), "OR-202603-005", daysAgo(4), daysAgo(1), amount("7440000"), SalesVoucherStatus.UNPAID, "휠 아치 라이너 주간 전표"),
            new SalesVoucherSeed("sv-demo-006", "SV-202603-006", users.get(5).getCustomerCompany(), "OR-202603-006", daysAgo(2), daysAgo(0), amount("3984000"), SalesVoucherStatus.PAID, "배터리 브래킷 출고 완료 전표")
        );
    }

    private List<PurchaseVoucherSeed> purchaseVoucherSeeds() {
        return List.of(
            new PurchaseVoucherSeed("pv-demo-001", "PV-202601-001", "019a52d5-0df8-724b-a16f-7a9d3bcd5384", "po-demo-002", daysAgo(44), daysAgo(30), amount("3870000"), PurchaseVoucherStatus.PENDING, "크롬 트림 매입 전표"),
            new PurchaseVoucherSeed("pv-demo-002", "PV-202601-002", "019a3df1-7843-7590-a5fd-94aa9aae7d0a", "po-demo-003", daysAgo(32), daysAgo(20), amount("2700000"), PurchaseVoucherStatus.UNPAID, "범퍼 브래킷 모듈 매입 전표"),
            new PurchaseVoucherSeed("pv-demo-003", "PV-202602-003", "019a52d4-7141-7a42-8674-a4c6597acfd7", "po-demo-004", daysAgo(24), daysAgo(10), amount("3420000"), PurchaseVoucherStatus.PAID, "접착 자재 매입 전표"),
            new PurchaseVoucherSeed("pv-demo-004", "PV-202602-004", "019a52d5-0df8-724b-a16f-7a9d3bcd5384", "po-demo-005", daysAgo(18), daysAgo(5), amount("2052000"), PurchaseVoucherStatus.PENDING, "도어 트림 클립 매입 전표"),
            new PurchaseVoucherSeed("pv-demo-005", "PV-202603-005", "019a52d4-ab46-7abe-9071-025222fb6144", "po-demo-007", daysAgo(6), daysAgo(2), amount("2736000"), PurchaseVoucherStatus.UNPAID, "트림 가니시 캐리어 매입 전표"),
            new PurchaseVoucherSeed("pv-demo-006", "PV-202603-006", "019a52d4-96be-72cb-85dd-19fbe3d80880", "po-demo-008", daysAgo(2), daysAgo(0), amount("3360000"), PurchaseVoucherStatus.PAID, "체결 하드웨어 매입 전표")
        );
    }

    private List<LeaveSeed> leaveSeeds(List<Employee> employees) {
        return List.of(
            new LeaveSeed("lv-demo-001", employees.get(0), LeaveType.ANNUAL, daysAgo(20), daysAgo(19), 1, "라인 교대 후 연차", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.APPROVED, daysAgo(24)),
            new LeaveSeed("lv-demo-002", employees.get(1), LeaveType.ANNUAL, daysAgo(16), daysAgo(15), 1, "창고 시스템 교육 참석", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.APPROVED, daysAgo(18)),
            new LeaveSeed("lv-demo-003", employees.get(2), LeaveType.SICK, daysAgo(14), daysAgo(13), 1, "감기 증상", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.APPROVED, daysAgo(15)),
            new LeaveSeed("lv-demo-004", employees.get(3), LeaveType.ANNUAL, daysAgo(8), daysAgo(7), 1, "가족 행사", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.REJECTED, daysAgo(9)),
            new LeaveSeed("lv-demo-005", employees.get(4), LeaveType.ANNUAL, daysAgo(2), daysAgo(1), 1, "개인 일정", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.APPROVED, daysAgo(4)),
            new LeaveSeed("lv-demo-006", employees.get(5), LeaveType.ANNUAL, daysAgo(-3), daysAgo(-2), 1, "주간 출하 후 휴가", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.PENDING, daysAgo(1)),
            new LeaveSeed("lv-demo-007", employees.get(6), LeaveType.SICK, daysAgo(-6), daysAgo(-5), 1, "치과 진료", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.PENDING, daysAgo(0)),
            new LeaveSeed("lv-demo-008", employees.get(7), LeaveType.ANNUAL, daysAgo(-10), daysAgo(-8), 2, "설비 점검 후 휴식", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.PENDING, daysAgo(0)),
            new LeaveSeed("lv-demo-009", employees.get(8), LeaveType.ANNUAL, daysAgo(6), daysAgo(5), 1, "현장 지원 후 대체 휴무", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.APPROVED, daysAgo(8)),
            new LeaveSeed("lv-demo-010", employees.get(9), LeaveType.ANNUAL, daysAgo(-14), daysAgo(-13), 1, "검사 라인 교대", org.ever._4ever_be_business.hr.enums.LeaveRequestStatus.REJECTED, daysAgo(1))
        );
    }

    private List<EmployeeTrainingSeed> employeeTrainingSeeds(List<Employee> employees, Map<String, Training> trainingByName) {
        return List.of(
            new EmployeeTrainingSeed("et-demo-001", employees.get(0), trainingByName.get("생산 안전 기본 교육"), TrainingCompletionStatus.COMPLETED, 30),
            new EmployeeTrainingSeed("et-demo-002", employees.get(1), trainingByName.get("MES 운영 실무"), TrainingCompletionStatus.IN_PROGRESS, 18),
            new EmployeeTrainingSeed("et-demo-003", employees.get(2), trainingByName.get("외장 사출 품질 관리"), TrainingCompletionStatus.COMPLETED, 26),
            new EmployeeTrainingSeed("et-demo-004", employees.get(3), trainingByName.get("창고 바코드 운영"), TrainingCompletionStatus.IN_PROGRESS, 12),
            new EmployeeTrainingSeed("et-demo-005", employees.get(4), trainingByName.get("구매 윤리 및 공급사 대응"), TrainingCompletionStatus.COMPLETED, 8),
            new EmployeeTrainingSeed("et-demo-006", employees.get(5), trainingByName.get("8D 품질 대응"), TrainingCompletionStatus.COMPLETED, 40),
            new EmployeeTrainingSeed("et-demo-007", employees.get(6), trainingByName.get("생산 안전 기본 교육"), TrainingCompletionStatus.IN_PROGRESS, 6),
            new EmployeeTrainingSeed("et-demo-008", employees.get(7), trainingByName.get("MES 운영 실무"), TrainingCompletionStatus.COMPLETED, 20),
            new EmployeeTrainingSeed("et-demo-009", employees.get(8), trainingByName.get("창고 바코드 운영"), TrainingCompletionStatus.COMPLETED, 14),
            new EmployeeTrainingSeed("et-demo-010", employees.get(9), trainingByName.get("구매 윤리 및 공급사 대응"), TrainingCompletionStatus.IN_PROGRESS, 4),
            new EmployeeTrainingSeed("et-demo-011", employees.get(10), trainingByName.get("외장 사출 품질 관리"), TrainingCompletionStatus.IN_PROGRESS, 10),
            new EmployeeTrainingSeed("et-demo-012", employees.get(11), trainingByName.get("8D 품질 대응"), TrainingCompletionStatus.COMPLETED, 35)
        );
    }

    private record QuotationSeed(
        String id,
        String approvalId,
        String code,
        CustomerUser customerUser,
        BigDecimal totalPrice,
        ApprovalStatus approvalStatus,
        String approvedByEmployeeId,
        String rejectedReason,
        LocalDateTime dueDate,
        int daysAgo,
        String note,
        List<QuotationItemSeed> items
    ) {
        private LocalDateTime approvedAt() {
            return approvedByEmployeeId == null ? null : BusinessDemoScenarioInitializer.daysAgo(Math.max(daysAgo - 1, 0));
        }
    }

    private record QuotationItemSeed(String id, String productId, Long count, BigDecimal price) {}

    private record OrderSeed(
        String id,
        String quotationId,
        String code,
        CustomerUser customerUser,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        LocalDateTime dueDate,
        OrderStatus status,
        int daysAgo,
        List<OrderItemSeed> items
    ) {}

    private record OrderItemSeed(String id, String productId, Long count, Long price) {}

    private record SalesVoucherSeed(
        String id,
        String code,
        CustomerCompany customerCompany,
        String orderCode,
        LocalDateTime issueDate,
        LocalDateTime dueDate,
        BigDecimal totalAmount,
        SalesVoucherStatus status,
        String memo
    ) {}

    private record PurchaseVoucherSeed(
        String id,
        String code,
        String supplierCompanyId,
        String productOrderId,
        LocalDateTime issueDate,
        LocalDateTime dueDate,
        BigDecimal totalAmount,
        PurchaseVoucherStatus status,
        String memo
    ) {}

    private record LeaveSeed(
        String id,
        Employee employee,
        LeaveType leaveType,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer leaveDays,
        String reason,
        org.ever._4ever_be_business.hr.enums.LeaveRequestStatus status,
        LocalDateTime createdAt
    ) {}

    private record EmployeeTrainingSeed(
        String id,
        Employee employee,
        Training training,
        TrainingCompletionStatus status,
        int daysAgo
    ) {}
}
