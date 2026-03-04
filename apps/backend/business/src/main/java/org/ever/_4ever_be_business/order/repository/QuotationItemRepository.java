package org.ever._4ever_be_business.order.repository;

import org.ever._4ever_be_business.order.entity.QuotationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationItemRepository extends JpaRepository<QuotationItem, String> {

    /**
     * 여러 견적 ID에 속한 견적 항목을 한 번에 조회
     */
    List<QuotationItem> findByQuotation_IdIn(List<String> quotationIds);
}
