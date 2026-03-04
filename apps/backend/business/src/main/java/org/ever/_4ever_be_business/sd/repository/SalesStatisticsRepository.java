package org.ever._4ever_be_business.sd.repository;

import org.ever._4ever_be_business.voucher.entity.SalesVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesStatisticsRepository extends JpaRepository<SalesVoucher, String>, SalesStatisticsRepositoryCustom {
}
