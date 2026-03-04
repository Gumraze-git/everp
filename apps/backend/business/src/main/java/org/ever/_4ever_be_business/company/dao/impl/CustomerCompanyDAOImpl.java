package org.ever._4ever_be_business.company.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.util.CodeGenerator;
import org.ever._4ever_be_business.company.dao.CustomerCompanyDAO;
import org.ever._4ever_be_business.company.dto.CustomerCreationResult;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.company.repository.CustomerCompanyRepository;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.sd.dto.request.CreateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.request.UpdateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerDetailDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListItemDto;
import org.ever._4ever_be_business.sd.vo.CustomerSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerCompanyDAOImpl implements CustomerCompanyDAO {

    private final CustomerCompanyRepository customerCompanyRepository;
    private final CustomerUserRepository customerUserRepository;

    @Override
    public Optional<CustomerDetailDto> findCustomerDetailById(String customerId) {
        return customerCompanyRepository.findCustomerDetailById(customerId);
    }

    @Override
    public Page<CustomerListItemDto> findCustomerList(CustomerSearchConditionVo condition, Pageable pageable) {
        return customerCompanyRepository.findCustomerList(condition, pageable);
    }

    @Override
    @Transactional
    public CustomerCreationResult saveCustomer(CreateCustomerRequestDto dto, String externalUserId) {
        // 1. 고객사 코드 생성 (UUID v7 기반)
        String customerCode = CodeGenerator.generateCustomerCode();

        // 2. CustomerCompany 엔티티 생성 및 저장
        CustomerCompany customerCompany = new CustomerCompany(
                null,  // customerUserId는 CustomerUser 저장 후 업데이트
                customerCode,
                dto.getCompanyName(),
                dto.getBusinessNumber(),
                dto.getCeoName(),
                dto.getZipCode(),
                dto.getAddress(),
                dto.getDetailAddress(),
                dto.getContactPhone(),
                dto.getContactEmail(),
                dto.getNote()
        );

        // 등록 DTO에 포함된 배송 리드타임(Duration)을 저장 (선택값)
        if (dto.getDeliveryLeadTime() != null) {
            customerCompany.updateDeliveryLeadTime(dto.getDeliveryLeadTime());
        }

        CustomerCompany savedCompany = customerCompanyRepository.save(customerCompany);

        CustomerUser savedManager = null;
        if (dto.getManager() != null) {
            CustomerUser customerUser = new CustomerUser(
                externalUserId,
                dto.getManager().getName(),
                savedCompany,
                null,  // customerUserCode - 필요시 생성 로직 추가
                dto.getManager().getEmail(),
                dto.getManager().getMobile()
            );
            savedManager = customerUserRepository.save(customerUser);
            if (savedManager != null) {
                savedCompany.assignCustomerUser(savedManager.getId());
            }
        }

        return new CustomerCreationResult(savedCompany, savedManager);
    }

    @Override
    @Transactional
    public void updateCustomer(String customerId, UpdateCustomerRequestDto dto) {
        // 1. 고객사 조회
        CustomerCompany customerCompany = customerCompanyRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        // 2. 고객사 기본 정보 수정
        customerCompany.updateInfo(
                dto.getCustomerName(),
                dto.getBusinessNumber(),
                dto.getCeoName(),
                dto.getBaseAddress(),
                dto.getDetailAddress(),
                dto.getCustomerPhone(),
                dto.getCustomerEmail(),
                dto.getNote()
        );

        // 3. 상태 변경
        if (dto.getStatusCode() != null) {
            customerCompany.updateStatus(dto.getStatusCode());
        }

        // 3-1. 배송 리드타임(Duration) 업데이트 (선택값)
        if (dto.getDeliveryLeadTime() != null) {
            customerCompany.updateDeliveryLeadTime(dto.getDeliveryLeadTime());
        }

        // 4. 담당자 정보 수정 (존재하는 경우)
        if (dto.getManager() != null) {
            // 기존 담당자 조회
            CustomerUser existingManager = customerUserRepository.findAll().stream()
                    .filter(cu -> cu.getCustomerCompany() != null &&
                            cu.getCustomerCompany().getId().equals(customerId))
                    .findFirst()
                    .orElse(null);

            if (existingManager != null) {
                // 기존 담당자 정보 수정
                existingManager.updateManagerInfo(
                        dto.getManager().getName(),
                        dto.getManager().getEmail(),
                        dto.getManager().getMobile()
                );
            } else {
                // 담당자가 없으면 새로 생성
                CustomerUser newManager = new CustomerUser(
                        null,  // userId - 향후 Auth 서비스와 연동 시 사용
                        dto.getManager().getName(),
                        customerCompany,
                        null,  // customerUserCode - 필요시 생성 로직 추가
                        dto.getManager().getEmail(),
                        dto.getManager().getMobile()
                );
                customerUserRepository.save(newManager);
            }
        }

        // customerCompanyRepository.save()는 필요 없음 - @Transactional이 변경 감지
    }

    @Override
    @Transactional
    public void deleteCustomer(String customerId) {
        // 고객사 조회
        CustomerCompany customerCompany = customerCompanyRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        // Soft Delete
        customerCompany.deactivate();

        // customerCompanyRepository.save()는 필요 없음 - @Transactional이 변경 감지
    }
}
