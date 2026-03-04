package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, String> {
    Optional<Department> findByDepartmentCode(String departmentCode);
}
