package org.ever._4ever_be_business.order.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.order.entity.Quotation;
import org.ever._4ever_be_business.order.entity.QuotationItem;
import org.ever._4ever_be_business.order.enums.ApprovalStatus;
import org.ever._4ever_be_business.order.repository.QuotationRepositoryCustom;
import org.ever._4ever_be_business.sd.dto.response.QuotationDetailDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationItemDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;
import org.ever._4ever_be_business.sd.dto.response.ScmQuotationListItemDto;
import org.ever._4ever_be_business.sd.vo.QuotationSearchConditionVo;
import org.ever._4ever_be_business.sd.vo.ScmQuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.order.entity.QQuotation.quotation;
import static org.ever._4ever_be_business.order.entity.QQuotationItem.quotationItem;
import static org.ever._4ever_be_business.order.entity.QQuotationApproval.quotationApproval;
import static org.ever._4ever_be_business.hr.entity.QCustomerUser.customerUser;
import static org.ever._4ever_be_business.company.entity.QCustomerCompany.customerCompany;

@Repository
@RequiredArgsConstructor
public class QuotationRepositoryImpl implements QuotationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Optional<QuotationDetailDto> findQuotationDetailById(String quotationId) {
        // Repository는 기본 정보만 반환, Product 정보와 Customer 정보는 Service에서 채움
        // 빈 DTO를 반환하고 Service에서 채우도록 변경
        // 실제로는 Quotation 엔티티를 조회하는 것이 더 나을 수 있음

        // Quotation이 존재하는지만 확인
        Long count = queryFactory
                .select(quotation.count())
                .from(quotation)
                .where(quotation.id.eq(quotationId))
                .fetchOne();

        if (count == null || count == 0) {
            return Optional.empty();
        }

        // 빈 DTO 반환 (Service에서 채울 예정)
        return Optional.of(new QuotationDetailDto(
                null, null, null, null, null, null, null, null, null
        ));
    }

    @Override
    public Page<QuotationListItemDto> findQuotationList(QuotationSearchConditionVo condition, Pageable pageable) {
        // 1. 동적 쿼리 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // 견적 ID 필터
        if (condition.getQuotationId() != null && !condition.getQuotationId().isEmpty()) {
            builder.and(quotation.id.eq(condition.getQuotationId()));
        }

        // 고객사 사용자 ID 필터 (CUSTOMER 유저용)
        if (condition.getCustomerId() != null && !condition.getCustomerId().isEmpty()) {
            builder.and(quotation.customerUserId.eq(condition.getCustomerId()));
        }

        // 날짜 범위 필터 (startDate ~ endDate)
        if (condition.getStartDate() != null && !condition.getStartDate().isEmpty()) {
            java.time.LocalDate startDate = java.time.LocalDate.parse(condition.getStartDate(), DATE_FORMATTER);
            builder.and(quotation.createdAt.goe(startDate.atStartOfDay()));
        }
        if (condition.getEndDate() != null && !condition.getEndDate().isEmpty()) {
            java.time.LocalDate endDate = java.time.LocalDate.parse(condition.getEndDate(), DATE_FORMATTER);
            builder.and(quotation.createdAt.loe(endDate.atTime(23, 59, 59)));
        }

        // 상태 필터
        if (condition.getStatus() != null && !condition.getStatus().equalsIgnoreCase("ALL")) {
            try {
                ApprovalStatus status = ApprovalStatus.valueOf(condition.getStatus());
                builder.and(quotationApproval.approvalStatus.eq(status));
            } catch (IllegalArgumentException e) {
                // 잘못된 상태값은 무시
            }
        }

        // 검색 조건 (type과 search 모두 있을 때만 검색)
        if (condition.getType() != null && !condition.getType().isEmpty() &&
                condition.getSearch() != null && !condition.getSearch().isEmpty()) {

            String search = "%" + condition.getSearch().trim() + "%";

            switch (condition.getType().toLowerCase()) {
                case "quotationnumber" -> builder.and(quotation.quotationCode.like(search));
                case "customername" -> builder.and(customerCompany.companyName.like(search));
                case "managername" -> builder.and(customerUser.customerName.like(search));
            }
        }

        // 2. 정렬 조건
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(condition.getSort());

        // 3. 데이터 조회 (첫 번째 QuotationItem 정보 포함)
        JPAQuery<QuotationListItemDto> query = queryFactory
                .select(Projections.constructor(
                        QuotationListItemDto.class,
                        quotation.id,                              // quotationId
                        quotation.quotationCode,                   // quotationNumber
                        customerCompany.companyName,               // customerName
                        customerUser.customerName,                 // managerName
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                quotation.createdAt
                        ),                                         // quotationDate
                        quotationItem.productId,                   // productId (첫 번째 아이템)
                        quotation.totalPrice,                      // totalAmount - BigDecimal
                        Expressions.stringTemplate(
                                "CASE WHEN {0} IS NULL THEN '-' ELSE TO_CHAR({0}, 'YYYY-MM-DD') END",
                                quotation.dueDate
                        ),                                         // dueDate
                        quotationItem.count,                       // quantity (첫 번째 아이템)
                        quotationItem.unit.stringValue(),          // uomName (첫 번째 아이템)
                        quotationApproval.approvalStatus.stringValue()  // statusCode
                ))
                .from(quotation)
                .leftJoin(quotation.quotationApproval, quotationApproval)
                .leftJoin(customerUser).on(customerUser.id.eq(quotation.customerUserId))
                .leftJoin(customerUser.customerCompany, customerCompany)
                .leftJoin(quotationItem).on(quotationItem.quotation.id.eq(quotation.id)
                        .and(quotationItem.id.eq(
                                queryFactory.select(quotationItem.id.min())
                                        .from(quotationItem)
                                        .where(quotationItem.quotation.id.eq(quotation.id))
                        )))
                .where(builder)
                .orderBy(orderSpecifier);

        // 4. 페이징 적용
        List<QuotationListItemDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 5. 전체 개수 조회
        Long total = queryFactory
                .select(quotation.count())
                .from(quotation)
                .leftJoin(quotation.quotationApproval, quotationApproval)
                .leftJoin(customerUser).on(customerUser.id.eq(quotation.customerUserId))
                .leftJoin(customerUser.customerCompany, customerCompany)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        if (sort == null || sort.isEmpty()) {
            return quotation.createdAt.desc(); // 기본값: 견적일자 내림차순
        }

        return switch (sort.toLowerCase()) {
            case "asc" -> quotation.createdAt.asc();
            case "desc" -> quotation.createdAt.desc();
            default -> quotation.createdAt.desc();
        };
    }

    private String mapStatusLabel(String statusCode) {
        if (statusCode == null) {
            return "대기";
        }

        return switch (statusCode) {
            case "PENDING" -> "대기";
            case "APPROVED" -> "승인";
            case "REJECTED" -> "거부";
            case "REVIEW" -> "검토중";
            default -> "대기";
        };
    }

    @Override
    public Page<ScmQuotationListItemDto> findScmQuotationList(ScmQuotationSearchConditionVo condition, Pageable pageable) {
        // 1. 동적 쿼리 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // 날짜 범위 필터 (createdAt 기준)
        if (condition.getStartDate() != null && !condition.getStartDate().isEmpty()) {
            java.time.LocalDate startDate = java.time.LocalDate.parse(condition.getStartDate(), DATE_FORMATTER);
            builder.and(quotation.createdAt.goe(startDate.atStartOfDay()));
        }
        if (condition.getEndDate() != null && !condition.getEndDate().isEmpty()) {
            java.time.LocalDate endDate = java.time.LocalDate.parse(condition.getEndDate(), DATE_FORMATTER);
            builder.and(quotation.createdAt.loe(endDate.atTime(23, 59, 59)));
        }

        // 상태 필터 (statusCode)
        if (condition.getStatusCode() != null && !condition.getStatusCode().isEmpty()) {
            String statusCode = condition.getStatusCode().toUpperCase();

            if (statusCode.equals("ALL")) {
                // ALL: REVIEW OR (APPROVAL && CHECKED) OR (REJECTED && CHECKED)
                BooleanBuilder statusBuilder = new BooleanBuilder();
                statusBuilder.or(quotationApproval.approvalStatus.eq(ApprovalStatus.REVIEW));
                statusBuilder.or(
                    quotationApproval.approvalStatus.eq(ApprovalStatus.APPROVAL)
                    .and(quotation.availableStatus.eq("CHECKED"))
                );
                statusBuilder.or(
                    quotationApproval.approvalStatus.eq(ApprovalStatus.REJECTED)
                    .and(quotation.availableStatus.eq("CHECKED"))
                );
                builder.and(statusBuilder);
            } else if (statusCode.equals("REVIEW")) {
                // REVIEW만 조회
                builder.and(quotationApproval.approvalStatus.eq(ApprovalStatus.REVIEW));
            } else if (statusCode.equals("APPROVAL")) {
                // APPROVAL && CHECKED
                builder.and(quotationApproval.approvalStatus.eq(ApprovalStatus.APPROVAL));
                builder.and(quotation.availableStatus.eq("CHECKED"));
            } else if (statusCode.equals("REJECTED")) {
                // REJECTED && CHECKED
                builder.and(quotationApproval.approvalStatus.eq(ApprovalStatus.REJECTED));
                builder.and(quotation.availableStatus.eq("CHECKED"));
            }
        }

        // availableStatus 필터
        if (condition.getAvailableStatus() != null && !condition.getAvailableStatus().isEmpty()) {
            String availStatus = condition.getAvailableStatus().toUpperCase();
            if (availStatus.equals("CHECKED")) {
                builder.and(quotation.availableStatus.eq("CHECKED"));
            } else if (availStatus.equals("UNCHECKED")) {
                // UNCHECKED인 것만 조회
                builder.and(quotation.availableStatus.eq("UNCHECKED"));
            }
        }

        // 2. Quotation 기본 정보 조회 (페이징 적용)
        List<Quotation> quotations = queryFactory
                .selectFrom(quotation)
                .innerJoin(quotation.quotationApproval, quotationApproval).fetchJoin()
                .where(builder)
                .orderBy(quotation.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 3. QuotationItem들을 조회하고 DTO로 변환
        List<ScmQuotationListItemDto> content = quotations.stream().map(q -> {
            // 해당 Quotation의 모든 QuotationItem 조회
            List<QuotationItem> items = queryFactory
                    .selectFrom(quotationItem)
                    .where(quotationItem.quotation.id.eq(q.getId()))
                    .fetch();

            // QuotationItem을 ScmQuotationItemDto로 변환
            List<org.ever._4ever_be_business.sd.dto.response.ScmQuotationItemDto> itemDtos = items.stream()
                    .map(item -> new org.ever._4ever_be_business.sd.dto.response.ScmQuotationItemDto(
                            item.getProductId(),  // quotation_item의 product_id
                            item.getCount()
                    ))
                    .toList();

            // CustomerCompany 이름 조회
            String customerName = queryFactory
                    .select(customerCompany.companyName)
                    .from(customerUser)
                    .innerJoin(customerUser.customerCompany, customerCompany)
                    .where(customerUser.id.eq(q.getCustomerUserId()))
                    .fetchOne();

            // requestDate (createdAt)
            String requestDate = q.getCreatedAt().format(DATE_FORMATTER);

            // dueDate (UNCHECKED면 "-", CHECKED면 dueDate)
            String dueDate;
            if (q.getAvailableStatus() != null && q.getAvailableStatus().equals("CHECKED")) {
                dueDate = q.getDueDate().format(DATE_FORMATTER);
            } else {
                dueDate = "-";
            }

            // availableStatus (null이면 UNCHECKED)
            String availableStatus = (q.getAvailableStatus() == null || q.getAvailableStatus().isEmpty())
                    ? "UNCHECKED"
                    : q.getAvailableStatus();

            return new ScmQuotationListItemDto(
                    q.getId(),
                    q.getQuotationCode(),
                    customerName != null ? customerName : "",
                    requestDate,
                    dueDate,
                    itemDtos,
                    q.getQuotationApproval().getApprovalStatus().name(),
                    availableStatus
            );
        }).toList();

        // 4. 전체 개수 조회
        Long total = queryFactory
                .select(quotation.count())
                .from(quotation)
                .innerJoin(quotation.quotationApproval, quotationApproval)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
