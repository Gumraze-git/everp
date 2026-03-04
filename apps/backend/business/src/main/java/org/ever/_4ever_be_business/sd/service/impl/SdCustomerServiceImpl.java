package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.async.AsyncResultManager;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.saga.SagaTransactionManager;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.company.dao.CustomerCompanyDAO;
import org.ever._4ever_be_business.company.dto.CustomerCreationResult;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.sd.dto.request.CreateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.request.UpdateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerDetailDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListItemDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListResponseDto;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;
import org.ever._4ever_be_business.sd.integration.port.CustomerUserServicePort;
import org.ever._4ever_be_business.sd.service.SdCustomerService;
import org.ever._4ever_be_business.sd.vo.CustomerDetailVo;
import org.ever._4ever_be_business.sd.vo.CustomerSearchConditionVo;
import org.ever.event.CreateAuthUserResultEvent;
import org.ever.event.CreateCustomerUserEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class SdCustomerServiceImpl implements SdCustomerService {

    private final CustomerCompanyDAO customerCompanyDAO;
    @SuppressWarnings("rawtypes")
    private final AsyncResultManager asyncResultManager;
    private final SagaTransactionManager sagaTransactionManager;
    private final CustomerUserServicePort customerUserServicePort;

    @Override
    @Transactional(readOnly = true)
    public CustomerDetailDto getCustomerDetail(CustomerDetailVo vo) {
        log.info("고객사 상세 정보 조회 요청 - customerId: {}", vo.getCustomerId());

        CustomerDetailDto result = customerCompanyDAO.findCustomerDetailById(vo.getCustomerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        log.info("고객사 상세 정보 조회 성공 - customerName: {}", result.getCustomerName());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerListResponseDto getCustomerList(CustomerSearchConditionVo condition, Pageable pageable) {
        log.info("고객사 목록 조회 요청 - status: {}, type: {}, search: {}",
                condition.getStatus(), condition.getType(), condition.getSearch());

        Page<CustomerListItemDto> page = customerCompanyDAO.findCustomerList(condition, pageable);

        // Page 객체를 CustomerListResponseDto로 변환
        PageInfo pageInfo = new PageInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );

        CustomerListResponseDto result = new CustomerListResponseDto(
                page.getContent(),
                pageInfo
        );

        log.info("고객사 목록 조회 성공 - totalElements: {}, totalPages: {}",
                page.getTotalElements(), page.getTotalPages());

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public void createCustomer(
        CreateCustomerRequestDto dto,
        DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> deferredResult
    ) {
        log.info("[SAGA] 고객사 등록 비동기 처리 시작 - companyName: {}, businessNumber: {}",
            dto.getCompanyName(), dto.getBusinessNumber());

        if (dto.getManager() == null) {
            log.warn("[SAGA][FAIL] 고객사 담당자 정보가 없습니다.");
            deferredResult.setResult(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail("고객사 담당자 정보는 필수입니다.", HttpStatus.BAD_REQUEST))
            );
            return;
        }

        String transactionId = UuidV7Generator.generate();
        asyncResultManager.registerResult(transactionId, deferredResult);

        sagaTransactionManager.executeSagaWithId(transactionId, () -> {
            try {
                String externalUserId = UuidV7Generator.generate();

                CustomerCreationResult creationResult = customerCompanyDAO.saveCustomer(dto, externalUserId);
                CustomerCompany savedCompany = creationResult.customerCompany();
                CustomerUser customerManager = creationResult.customerUser();

                if (savedCompany == null || customerManager == null) {
                    log.error("[SAGA][FAIL] 고객사 또는 담당자 저장 결과가 유효하지 않습니다. savedCompany: {}, customerManager: {}",
                        savedCompany, customerManager);
                    asyncResultManager.setErrorResult(
                        transactionId,
                        "[SAGA][FAIL] 고객사 또는 담당자 저장에 실패했습니다.",
                        HttpStatus.INTERNAL_SERVER_ERROR
                    );
                    return null;
                }

                CreateCustomerUserEvent event = CreateCustomerUserEvent.builder()
                    .eventId(UuidV7Generator.generate())
                    .transactionId(transactionId)
                    .customerCompanyId(savedCompany.getId())
                    .customerCompanyName(savedCompany.getCompanyName())
                    .customerCompanyCode(savedCompany.getCompanyCode())
                    .customerUserId(customerManager.getId())
                    .userId(externalUserId)
                    .managerName(customerManager.getCustomerName())
                    .managerEmail(customerManager.getEmail())
                    .managerPhone(customerManager.getPhoneNumber())
                    .build();

                customerUserServicePort.createCustomerUser(event)
                    .exceptionally(error -> {
                        log.error("[SAGA][FAIL] 고객사 사용자 계정 생성 이벤트 발행 실패 - txId: {}, cause: {}",
                            transactionId, error.getMessage(), error);
                        asyncResultManager.setErrorResult(
                            transactionId,
                            "[SAGA][FAIL] 고객사 사용자 계정 생성 요청 발행 실패: " + error.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                        );
                        return null;
                    });

                log.info("[SAGA] 고객사 등록 처리 완료 - txId: {}, customerId: {}, managerId: {}",
                    transactionId, savedCompany.getId(), customerManager.getId());
                return null;
            } catch (Exception error) {
                log.error("[SAGA][FAIL] 고객사 등록 처리 중 예외 발생 - txId: {}, cause: {}", transactionId, error.getMessage(), error);
                asyncResultManager.setErrorResult(
                    transactionId,
                    "[SAGA][FAIL] 고객사 등록 처리에 실패했습니다.: " + error.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
                );
                throw error;
            }
        });
    }

    @Override
    @Transactional
    public void updateCustomer(String customerId, UpdateCustomerRequestDto dto) {
        log.info("고객사 정보 수정 요청 - customerId: {}, customerName: {}",
                customerId, dto.getCustomerName());

        customerCompanyDAO.updateCustomer(customerId, dto);

        log.info("고객사 정보 수정 성공 - customerId: {}", customerId);
    }

    @Override
    @Transactional
    public void deleteCustomer(String customerId) {
        log.info("고객사 삭제 요청 - customerId: {}", customerId);

        customerCompanyDAO.deleteCustomer(customerId);

        log.info("고객사 삭제 성공 (Soft Delete) - customerId: {}", customerId);
    }
}
