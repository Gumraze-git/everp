package org.ever._4ever_be_business.company.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.common.jpa.converter.DurationToSecondsConverter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Duration;

@Entity
@Table(name="customer_company")
@Getter
@NoArgsConstructor
public class CustomerCompany extends TimeStamp {

    @Id
    @Column(length = 36, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name="customer_user_id")
    private String customerUserId;

    @Column(name="company_code", length = 20)
    private String companyCode;

    @Column(name="company_name", length = 100)
    private String companyName;

    @Column(name="business_number", length = 20)
    private String businessNumber;

    @Column(name="ceo_name", length = 50)
    private String ceoName;

    @Column(name="zip_code", length = 10)
    private String zipCode;

    @Column(name="base_address", length = 255)
    private String baseAddress;

    @Column(name="detail_address", length = 255)
    private String detailAddress;

    @Column(name="office_phone", length = 20)
    private String officePhone;

    @Column(name="office_email", length = 20)
    private String officeEmail;

    @Column(name="etc", length = 255)
    private String etc;

    @Column(name="is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "delivery_lead_time")
    @Convert(converter = DurationToSecondsConverter.class)
    private Duration deliveryLeadTime;

    public CustomerCompany(String id, String customerUserId, String companyCode, String companyName, String businessNumber, String ceoName, String zipCode, String baseAddress, String detailAddress, String officePhone, String officeEmail, String etc) {
        this.id = id;
        this.customerUserId = customerUserId;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.businessNumber = businessNumber;
        this.ceoName = ceoName;
        this.zipCode = zipCode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.officePhone = officePhone;
        this.officeEmail = officeEmail;
        this.etc = etc;
        this.isActive = true;
    }

    public CustomerCompany(String customerUserId, String companyCode, String companyName, String businessNumber, String ceoName, String zipCode, String baseAddress, String detailAddress, String officePhone, String officeEmail, String etc) {
        this(null, customerUserId, companyCode, companyName, businessNumber, ceoName, zipCode, baseAddress, detailAddress, officePhone, officeEmail, etc);
    }

    /**
     * 고객사 정보 수정
     */
    public void updateInfo(String companyName, String businessNumber, String ceoName,
                          String baseAddress, String detailAddress,
                          String officePhone, String officeEmail, String etc) {
        this.companyName = companyName;
        this.businessNumber = businessNumber;
        this.ceoName = ceoName;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.officePhone = officePhone;
        this.officeEmail = officeEmail;
        this.etc = etc;
    }

    public void updateDeliveryLeadTime(Duration deliveryLeadTime) {
        this.deliveryLeadTime = deliveryLeadTime;
    }

    /**
     * 고객사 상태 변경
     */
    public void updateStatus(String statusCode) {
        if ("ACTIVE".equalsIgnoreCase(statusCode)) {
            this.isActive = true;
        } else if ("INACTIVE".equalsIgnoreCase(statusCode)) {
            this.isActive = false;
        }
    }

    /**
     * 고객사 비활성화 (Soft Delete)
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 고객사 담당자 지정
     */
    public void assignCustomerUser(String customerUserId) {
        this.customerUserId = customerUserId;
    }

    /**
     * 고객사 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
