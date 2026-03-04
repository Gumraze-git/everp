package org.ever._4ever_be_business.hr.dao.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.dao.PositionDAO;
import org.ever._4ever_be_business.hr.dto.response.PositionDetailDto;
import org.ever._4ever_be_business.hr.dto.response.PositionEmployeeDto;
import org.ever._4ever_be_business.hr.dto.response.PositionListItemDto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QDepartment.department;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PositionDAOImpl implements PositionDAO {

    private final JPAQueryFactory queryFactory;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Optional<PositionDetailDto> findPositionDetailById(String positionId) {
        log.debug("직급 상세 정보 조회 시작 - positionId: {}", positionId);

        // 1. Position 기본 정보 조회
        var positionInfo = queryFactory
                .select(Projections.constructor(
                        PositionInfoProjection.class,
                        position.id,
                        position.positionCode,
                        position.positionName,
                        position.salary
                ))
                .from(position)
                .where(position.id.eq(positionId))
                .fetchOne();

        if (positionInfo == null) {
            log.debug("직급을 찾을 수 없음 - positionId: {}", positionId);
            return Optional.empty();
        }

        // 2. 해당 직급에 속한 직원 목록 조회
        List<PositionEmployeeDto> employees = queryFactory
                .select(Projections.constructor(
                        PositionEmployeeDto.class,
                        employee.id,
                        internelUser.employeeCode,
                        internelUser.name,
                        position.id,
                        position.positionName,
                        department.id,
                        department.departmentName,
                        internelUser.hireDate.stringValue() // InternelUser의 hireDate 사용
                ))
                .from(employee)
                .innerJoin(employee.internelUser, internelUser)
                .innerJoin(internelUser.position, position)
                .innerJoin(position.department, department)
                .where(position.id.eq(positionId))
                .orderBy(internelUser.hireDate.asc()) // hireDate 순으로 정렬
                .fetch();

        // hireDate 포맷팅
        List<PositionEmployeeDto> formattedEmployees = employees.stream()
                .map(emp -> {
                    try {
                        String formattedDate = emp.getHireDate();
                        if (formattedDate != null && formattedDate.length() >= 10) {
                            formattedDate = formattedDate.substring(0, 10);
                        }
                        return new PositionEmployeeDto(
                                emp.getEmployeeId(),
                                emp.getEmployeeCode(),
                                emp.getEmployeeName(),
                                emp.getPositionId(),
                                emp.getPosition(),
                                emp.getDepartmentId(),
                                emp.getDepartment(),
                                formattedDate
                        );
                    } catch (Exception e) {
                        log.warn("날짜 포맷팅 실패 - employeeId: {}", emp.getEmployeeId(), e);
                        return emp;
                    }
                })
                .toList();

        // 3. headCount 계산
        Long headCount = (long) formattedEmployees.size();

        // 4. DTO 조립
        PositionDetailDto result = new PositionDetailDto(
                positionInfo.getPositionId(),
                positionInfo.getPositionCode(),
                positionInfo.getPositionName(),
                headCount,
                positionInfo.getPayment(),
                formattedEmployees
        );

        log.debug("직급 상세 정보 조회 완료 - positionId: {}, positionName: {}, headCount: {}",
                positionId, positionInfo.getPositionName(), headCount);

        return Optional.of(result);
    }

    @Override
    public List<PositionListItemDto> findPositionList() {
        log.debug("직급 목록 조회 시작");

        // 1. 모든 Position 조회
        List<PositionInfoProjection> positions = queryFactory
                .select(Projections.constructor(
                        PositionInfoProjection.class,
                        position.id,
                        position.positionCode,
                        position.positionName,
                        position.salary
                ))
                .from(position)
                .orderBy(position.id.asc())
                .fetch();

        // 2. 각 Position별 직원 수 계산
        List<PositionListItemDto> result = positions.stream()
                .map(pos -> {
                    // 해당 직급의 직원 수 조회
                    Long headCount = queryFactory
                            .select(employee.count())
                            .from(employee)
                            .innerJoin(employee.internelUser, internelUser)
                            .innerJoin(internelUser.position, position)
                            .where(position.id.eq(pos.getPositionId()))
                            .fetchOne();

                    if (headCount == null) {
                        headCount = 0L;
                    }

                    return new PositionListItemDto(
                            pos.getPositionId(),
                            pos.getPositionName(),
                            headCount,
                            pos.getPayment()
                    );
                })
                .toList();

        log.debug("직급 목록 조회 완료 - count: {}", result.size());

        return result;
    }

    /**
     * QueryDSL Projection용 내부 클래스
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class PositionInfoProjection {
        private String positionId;
        private String positionCode;
        private String positionName;
        private BigDecimal payment;
    }
}
