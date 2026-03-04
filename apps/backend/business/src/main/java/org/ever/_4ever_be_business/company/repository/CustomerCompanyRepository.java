package org.ever._4ever_be_business.company.repository;

import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerCompanyRepository extends JpaRepository<CustomerCompany, String>, CustomerCompanyRepositoryCustom {
    Optional<CustomerCompany> findByCompanyCode(String companyCode);
}
