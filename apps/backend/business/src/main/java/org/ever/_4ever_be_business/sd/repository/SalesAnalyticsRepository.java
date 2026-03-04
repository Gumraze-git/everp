package org.ever._4ever_be_business.sd.repository;

import org.ever._4ever_be_business.voucher.entity.SalesVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesAnalyticsRepository extends JpaRepository<SalesVoucher, String>, SalesAnalyticsRepositoryCustom {
}
