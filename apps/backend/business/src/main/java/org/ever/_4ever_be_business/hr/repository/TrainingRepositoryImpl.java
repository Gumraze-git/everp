package org.ever._4ever_be_business.hr.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;
import org.ever._4ever_be_business.hr.vo.TrainingSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.ever._4ever_be_business.hr.entity.QTraining.training;

/**
 * Training Custom Repository 구현체
 * QueryDSL을 사용한 동적 쿼리 구현
 */
@Repository
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Training> searchTrainingPrograms(TrainingSearchConditionVo condition, Pageable pageable) {
        // 전체 개수 조회
        Long total = queryFactory
                .select(training.count())
                .from(training)
                .where(
                        nameLike(condition.getName()),
                        statusEq(condition.getStatus()),
                        categoryEq(condition.getCategory())
                )
                .fetchOne();

        // 페이징 데이터 조회
        List<Training> content = queryFactory
                .selectFrom(training)
                .where(
                        nameLike(condition.getName()),
                        statusEq(condition.getStatus()),
                        categoryEq(condition.getCategory())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(training.id.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * 교육 프로그램명 LIKE 검색 조건
     */
    private BooleanExpression nameLike(String name) {
        return StringUtils.hasText(name) ? training.trainingName.contains(name) : null;
    }

    /**
     * 교육 상태 일치 조건
     */
    private BooleanExpression statusEq(TrainingStatus status) {
        return status != null ? training.trainingStatus.eq(status) : null;
    }

    /**
     * 교육 카테고리 일치 조건
     */
    private BooleanExpression categoryEq(TrainingCategory category) {
        return category != null ? training.category.eq(category) : null;
    }
}
