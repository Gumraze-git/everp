package org.ever._4ever_be_business.hr.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.dto.response.PositionDetailDto;
import org.ever._4ever_be_business.hr.dto.response.PositionEmployeeDto;
import org.ever._4ever_be_business.hr.dto.response.PositionListItemDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QDepartment.department;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PositionRepositoryImpl implements PositionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PositionListItemDto> findPositionListWithHeadCount() {
        log.debug("직급 목록 조회 시작 (헤드카운트 포함)");

        List<PositionListItemDto> result = queryFactory
                .select(Projections.constructor(
                        PositionListItemDto.class,
                        position.id,
                        position.positionName,
                        internelUser.count(),
                        position.salary
                ))
                .from(position)
                .leftJoin(internelUser).on(internelUser.position.id.eq(position.id))
                .groupBy(position.id, position.positionName, position.salary)
                .orderBy(position.salary.asc())
                .fetch();

        log.debug("직급 목록 조회 완료 - count: {}", result.size());

        return result;
    }

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
            log.debug("직급 정보를 찾을 수 없음 - positionId: {}", positionId);
            return Optional.empty();
        }

        // 2. 해당 직급의 직원 목록 조회
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
                        Expressions.stringTemplate("TO_CHAR({0}, 'YYYY-MM-DD')", internelUser.hireDate)
                ))
                .from(employee)
                .innerJoin(employee.internelUser, internelUser)
                .innerJoin(internelUser.position, position)
                .innerJoin(position.department, department)
                .where(position.id.eq(positionId))
                .orderBy(internelUser.hireDate.desc())
                .fetch();

        // 3. DTO 조립
        PositionDetailDto result = new PositionDetailDto(
                positionInfo.getPositionId(),
                positionInfo.getPositionNumber(),
                positionInfo.getPositionName(),
                (long) employees.size(),
                positionInfo.getPayment(),
                employees
        );

        log.debug("직급 상세 정보 조회 완료 - positionId: {}, positionName: {}, employeeCount: {}",
                positionId, positionInfo.getPositionName(), employees.size());

        return Optional.of(result);
    }

    /**
     * QueryDSL Projection용 내부 클래스
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class PositionInfoProjection {
        private String positionId;
        private String positionNumber;
        private String positionName;
        private java.math.BigDecimal payment;
    }
}
