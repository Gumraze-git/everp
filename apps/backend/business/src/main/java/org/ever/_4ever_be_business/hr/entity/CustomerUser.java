package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.company.entity.CustomerCompany;

@Entity
@Table
@NoArgsConstructor
@Getter
public class CustomerUser extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name="user_id")
    private String userId;

    @Column(name="customer_name")
    private String customerName;

    @ManyToOne
    @JoinColumn(name="customer_company_id")
    private CustomerCompany customerCompany;

    @Column(name="customer_user_code")
    private String customerUserCode;

    @Column(name="email", length = 100)
    private String email;

    @Column(name="phone_number", length = 20)
    private String phoneNumber;

    public CustomerUser(String userId, String customerName, CustomerCompany customerCompany, String customerUserCode, String email, String phoneNumber) {
        this.userId = userId;
        this.customerName = customerName;
        this.customerCompany = customerCompany;
        this.customerUserCode = customerUserCode;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * 목업 데이터 생성용 생성자 (ID 포함)
     */
    public CustomerUser(String id, String userId, String customerName, CustomerCompany customerCompany, String customerUserCode, String email, String phoneNumber) {
        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.customerCompany = customerCompany;
        this.customerUserCode = customerUserCode;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * 담당자 정보 수정
     */
    public void updateManagerInfo(String customerName, String email, String phoneNumber) {
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void assignCompany(CustomerCompany customerCompany) {
        this.customerCompany = customerCompany;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
