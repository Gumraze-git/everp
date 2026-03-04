package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.PayrollDeducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PayrollDeducation Repository
 */
@Repository
public interface PayrollDeducationRepository extends JpaRepository<PayrollDeducation, String> {
}
