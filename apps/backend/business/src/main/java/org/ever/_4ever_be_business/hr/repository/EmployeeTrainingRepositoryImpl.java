package org.ever._4ever_be_business.hr.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.hr.entity.EmployeeTraining;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.ever._4ever_be_business.hr.entity.QEmployeeTraining.employeeTraining;
import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QDepartment.department;

/**
 * EmployeeTraining Custom Repository 구현체
 * QueryDSL을 사용한 동적 쿼리 구현
 */
@Repository
@RequiredArgsConstructor
public class EmployeeTrainingRepositoryImpl implements EmployeeTrainingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EmployeeTraining> findByTrainingIdWithAllRelations(String trainingId) {
        return queryFactory
                .selectFrom(employeeTraining)
                .join(employeeTraining.employee, employee).fetchJoin()
                .join(employee.internelUser, internelUser).fetchJoin()
                .join(internelUser.position, position).fetchJoin()
                .join(position.department, department).fetchJoin()
                .where(employeeTraining.training.id.eq(trainingId))
                .fetch();
    }

    @Override
    public List<EmployeeTraining> findByEmployeeIdWithTraining(String employeeId) {
        return queryFactory
                .selectFrom(employeeTraining)
                .join(employeeTraining.training).fetchJoin()
                .where(employeeTraining.employee.id.eq(employeeId))
                .orderBy(employeeTraining.updatedAt.desc())
                .fetch();
    }
}
