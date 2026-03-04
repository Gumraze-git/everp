package org.ever._4ever_be_business.tam.repository;

import org.ever._4ever_be_business.tam.entity.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, String>, VacationRequestRepositoryCustom {
}
