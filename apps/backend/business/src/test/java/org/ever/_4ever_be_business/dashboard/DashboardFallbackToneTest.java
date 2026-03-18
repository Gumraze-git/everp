package org.ever._4ever_be_business.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.fcm.service.ARInvoiceService;
import org.ever._4ever_be_business.fcm.service.PurchaseStatementService;
import org.ever._4ever_be_business.fcm.service.SupplierCompanyResolver;
import org.ever._4ever_be_business.fcm.service.impl.CustomerDashboardInvoiceServiceImpl;
import org.ever._4ever_be_business.fcm.service.impl.SupplierDashboardInvoiceServiceImpl;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.InternelUserRepository;
import org.ever._4ever_be_business.hr.repository.LeaveRequestRepository;
import org.ever._4ever_be_business.hr.service.impl.LeaveRequestServiceImpl;
import org.ever._4ever_be_business.order.repository.OrderItemRepository;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever._4ever_be_business.order.repository.QuotationItemRepository;
import org.ever._4ever_be_business.order.repository.QuotationRepository;
import org.ever._4ever_be_business.sd.integration.port.ProductServicePort;
import org.ever._4ever_be_business.sd.service.impl.DashboardCustomerQuotationServiceImpl;
import org.ever._4ever_be_business.sd.service.impl.DashboardOrderServiceImpl;
import org.ever._4ever_be_business.tam.dao.AttendanceDAO;
import org.ever._4ever_be_business.tam.repository.AttendanceRepository;
import org.ever._4ever_be_business.tam.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DashboardFallbackToneTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductServicePort productServicePort;

    @Mock
    private CustomerUserRepository customerUserRepository;

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private QuotationItemRepository quotationItemRepository;

    @Mock
    private ARInvoiceService arInvoiceService;

    @Mock
    private PurchaseStatementService purchaseStatementService;

    @Mock
    private SupplierCompanyResolver supplierCompanyResolver;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private InternelUserRepository internelUserRepository;

    @Mock
    private AttendanceDAO attendanceDAO;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Test
    void salesDashboardFallbackUsesOrderPrefixWithoutMockLabels() {
        when(orderRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(Page.empty());

        DashboardOrderServiceImpl service = new DashboardOrderServiceImpl(
                orderRepository,
                orderItemRepository,
                productServicePort,
                customerUserRepository
        );

        var items = service.getAllOrders(3);

        assertThat(items).hasSize(3);
        assertThat(items).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("OR-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).doesNotContain("목업").doesNotContain("MOCK");
            assertThat(item.getName()).doesNotContain("목업").doesNotContain("MOCK");
        });
    }

    @Test
    void customerQuotationFallbackUsesVehiclePartsQuotationNaming() {
        CustomerCompany company = new CustomerCompany(
                "company-1",
                "customer-user-1",
                "CUST-001",
                "Hanil Motors Co.",
                "123-45-67890",
                "박대표",
                "12345",
                "서울시 강남구 테헤란로 1",
                "10층",
                "02-1234-5678",
                "sales@hanil.example",
                null
        );
        CustomerUser user = new CustomerUser(
                "customer-1",
                "user-1",
                "정하준 차장",
                company,
                "CU-001",
                "contact@hanil.example",
                "010-1234-5678"
        );

        when(customerUserRepository.findByUserId("user-1")).thenReturn(Optional.of(user));
        when(quotationRepository.findByCustomerUserIdOrderByCreatedAtDesc(anyString(), any(Pageable.class)))
                .thenReturn(Page.empty());

        DashboardCustomerQuotationServiceImpl service = new DashboardCustomerQuotationServiceImpl(
                customerUserRepository,
                quotationRepository,
                quotationItemRepository,
                productServicePort
        );

        var items = service.getCustomerQuotations("user-1", 2);

        assertThat(items).hasSize(2);
        assertThat(items).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("QO-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).contains("Hanil Motors Co.").contains("견적 요청");
            assertThat(item.getName()).isEqualTo("정하준 차장");
        });
    }

    @Test
    void customerInvoiceFallbackUsesSalesVoucherPrefix() {
        when(arInvoiceService.getARInvoiceList(anyString(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Page.empty());

        CustomerDashboardInvoiceServiceImpl service = new CustomerDashboardInvoiceServiceImpl(arInvoiceService);

        var items = service.getCustomerInvoices("customer-company-1", 3);

        assertThat(items).hasSize(3);
        assertThat(items).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("SV-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).contains("매출 전표").doesNotContain("목업");
        });
    }

    @Test
    void supplierInvoiceFallbackUsesPurchaseVoucherPrefix() {
        when(supplierCompanyResolver.resolve("supplier-user-1"))
                .thenReturn(new SupplierCompanyResolver.SupplierCompanyInfo("supplier-company-1", "한빛 오토 외장"));
        when(purchaseStatementService.getPurchaseStatementListBySupplierUserId(anyString(), any(), any(), any(Pageable.class)))
                .thenReturn(Page.empty());
        when(purchaseStatementService.getPurchaseStatementList(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(Page.empty());

        SupplierDashboardInvoiceServiceImpl service = new SupplierDashboardInvoiceServiceImpl(
                purchaseStatementService,
                supplierCompanyResolver
        );

        var supplierItems = service.getSupplierInvoices("supplier-user-1", 2);
        var companyItems = service.getCompanyApInvoices(2);

        assertThat(supplierItems).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("PV-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).contains("매입 전표").doesNotContain("목업");
        });
        assertThat(companyItems).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("PV-").doesNotContain("MOCK");
            assertThat(item.getName()).doesNotContain("목업").doesNotContain("MOCK");
        });
    }

    @Test
    void leaveFallbackUsesRealLeaveRequestPrefix() {
        when(leaveRequestRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(Page.empty());

        LeaveRequestServiceImpl service = new LeaveRequestServiceImpl(
                leaveRequestRepository,
                employeeRepository,
                internelUserRepository
        );

        var items = service.getDashboardLeaveRequestList(null, 3);

        assertThat(items).hasSize(3);
        assertThat(items).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("LV-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).doesNotContain("목업").doesNotContain("MOCK");
        });
    }

    @Test
    void attendanceFallbackUsesAttendancePrefixAndReadableLabels() {
        when(attendanceRepository.findAllByOrderByWorkDateDesc(any(Pageable.class))).thenReturn(Page.empty());

        AttendanceServiceImpl service = new AttendanceServiceImpl(
                attendanceDAO,
                attendanceRepository,
                employeeRepository
        );

        var items = service.getDashboardAttendanceList(null, 3);

        assertThat(items).hasSize(3);
        assertThat(items).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("ATT-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).doesNotContain("목업").doesNotContain("MOCK");
            assertThat(item.getStatusCode()).isIn("NORMAL", "LATE");
        });
    }
}
