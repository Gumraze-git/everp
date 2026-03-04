package org.ever._4ever_be_business.order.repository;

import org.ever._4ever_be_business.order.entity.QuotationApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationApprovalRepository extends JpaRepository<QuotationApproval, String> {
}
