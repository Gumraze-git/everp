package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dto.response.CustomerUserDetailDto;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.hr.service.CustomerUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerUserServiceImpl implements CustomerUserService {

    private final CustomerUserRepository customerUserRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerUserDetailDto getCustomerUserDetailByUserId(String customerUserId) {
        log.info("CustomerUser ID로 고객 사용자 상세 정보 조회 요청 - customerUserId: {}", customerUserId);

        // CustomerUser 조회
        CustomerUser customerUser = customerUserRepository.findByUserId(customerUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "고객 사용자 정보를 찾을 수 없습니다."));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 주소 조합 (baseAddress + detailAddress)
        String address = "";
        if (customerUser.getCustomerCompany() != null) {
            String baseAddress = customerUser.getCustomerCompany().getBaseAddress() != null
                    ? customerUser.getCustomerCompany().getBaseAddress() : "";
            String detailAddress = customerUser.getCustomerCompany().getDetailAddress() != null
                    ? customerUser.getCustomerCompany().getDetailAddress() : "";

            if (!baseAddress.isEmpty() && !detailAddress.isEmpty()) {
                address = baseAddress + " " + detailAddress;
            } else {
                address = baseAddress + detailAddress;
            }
        }

        // 입사일 (createdAt)
        String joinDate = customerUser.getCreatedAt() != null
                ? customerUser.getCreatedAt().format(dateFormatter) : null;

        // 가입기간 계산 (개월 단위, 1부터 시작)
        Long membershipMonths = 1L;
        if (customerUser.getCreatedAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            long months = ChronoUnit.MONTHS.between(customerUser.getCreatedAt(), now);
            membershipMonths = months + 1; // 1부터 시작
        }

        // 회사명
        String companyName = customerUser.getCustomerCompany() != null
                ? customerUser.getCustomerCompany().getCompanyName() : null;

        CustomerUserDetailDto result = new CustomerUserDetailDto(
                customerUser.getUserId(),
                customerUser.getCustomerName(),
                customerUser.getCustomerUserCode(),
                customerUser.getEmail(),
                customerUser.getPhoneNumber(),
                address,
                joinDate,
                membershipMonths,
                companyName
        );

        log.info("CustomerUser ID로 고객 사용자 상세 정보 조회 성공 - customerUserId: {}, customerName: {}",
                customerUserId, customerUser.getCustomerName());

        return result;
    }
}
