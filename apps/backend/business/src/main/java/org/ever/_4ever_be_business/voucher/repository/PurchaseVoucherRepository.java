package org.ever._4ever_be_business.voucher.repository;

import org.ever._4ever_be_business.voucher.entity.PurchaseVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseVoucherRepository extends JpaRepository<PurchaseVoucher, String>, PurchaseVoucherRepositoryCustom {

    /**
     * 전표 코드로 시작하는 PurchaseVoucher 개수 조회 (코드 생성용)
     *
     * @param voucherCodePrefix 전표 코드 prefix
     * @return 해당 prefix로 시작하는 전표 개수
     */
    long countByVoucherCodeStartingWith(String voucherCodePrefix);
}
