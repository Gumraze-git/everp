package org.ever._4ever_be_business.order.repository;

import org.ever._4ever_be_business.order.entity.Quotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, String>, QuotationRepositoryCustom {

    Page<Quotation> findByCustomerUserIdOrderByCreatedAtDesc(String customerUserId, Pageable pageable);

    Page<Quotation> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Long countByCustomerUserId(String customerUserId);
}
