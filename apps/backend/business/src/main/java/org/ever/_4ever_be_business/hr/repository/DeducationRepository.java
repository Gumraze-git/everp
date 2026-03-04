package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Deducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Deducation Repository
 */
@Repository
public interface DeducationRepository extends JpaRepository<Deducation, String> {
}
