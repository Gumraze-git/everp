package org.ever._4ever_be_alarm.common.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.ever._4ever_be_alarm.common.response.PageDto;
import org.ever._4ever_be_alarm.common.response.PageResponseDto;
import org.springframework.data.domain.Page;

public final class PageResponseUtils {

    private static final PageDto EMPTY_PAGE_DTO = PageDto.builder()
        .number(0)
        .size(0)
        .totalElements(0)
        .totalPages(0)
        .hasNext(false)
        .build();

    private PageResponseUtils() {
    }

    /**
     * 기존 메서드: Map 형태로 페이지 정보 반환
     */
    @Deprecated
    public static Map<String, Object> buildPage(int number, int size, long totalElements) {
        int safeNumber = Math.max(number, 0);
        int safeSize = Math.max(size, 1);
        long safeTotal = Math.max(totalElements, 0);

        int totalPages = safeSize == 0 ? 0 : (int) Math.ceil((double) safeTotal / safeSize);
        boolean hasNext = safeNumber + 1 < totalPages;

        Map<String, Object> page = new LinkedHashMap<>();
        page.put("number", safeNumber);
        page.put("size", safeSize);
        page.put("totalElements", safeTotal);
        page.put("totalPages", totalPages);
        page.put("hasNext", hasNext);
        return page;
    }

    /**
     * Spring Page 객체를 PageDto로 변환
     */
    public static PageDto toPageDto(Page<?> page) {
        if (page == null || page.isEmpty()) {
            return EMPTY_PAGE_DTO;
        }

        int safeNumber = page.getNumber();
        int safeSize = Math.max(page.getSize(), 1);
        long safeTotal = Math.max(page.getTotalElements(), 0);
        int totalPages = safeSize == 0 ? 0 : (int) Math.ceil((double) safeTotal / safeSize);
        boolean hasNext = page.hasNext();

        return PageDto.builder()
            .number(safeNumber)
            .size(safeSize)
            .totalElements((int) safeTotal)
            .totalPages(totalPages)
            .hasNext(hasNext)
            .build();
    }

    /**
     * Spring Page 객체를 PageResponseDto로 변환
     */
    public static <T> PageResponseDto<T> toPageResponseDto(Page<T> page) {
        if (page == null || page.isEmpty()) {
            return buildEmptyPageResponseDto();
        }

        return PageResponseDto.<T>builder()
            .items(page.getContent())
            .page(toPageDto(page))
            .build();
    }

    /**
     * Spring Page 객체를 PageResponseDto로 변환 (데이터 변환 포함)
     */
    public static <T, R> PageResponseDto<R> toPageResponseDto(
        Page<T> page,
        java.util.function.Function<? super T, ? extends R> mapper
    ) {
        if (page == null || page.isEmpty()) {
            return buildEmptyPageResponseDto();
        }

        List<R> mappedContent = page.getContent().stream()
            .map(mapper)
            .collect(Collectors.toList());

        return PageResponseDto.<R>builder()
            .items(mappedContent)
            .page(toPageDto(page))
            .build();
    }

    /**
     * 비어있는 PageResponseDto를 생성하는 helper 메서드.
     *
     * @param <T> 데이터 아이템 타입
     * @return 비어있는 PageResponseDto 객체
     */
    private static <T> PageResponseDto<T> buildEmptyPageResponseDto() {
        return PageResponseDto.<T>builder()
            .items(Collections.emptyList()) // 빈 리스트 명시적 사용
            .page(EMPTY_PAGE_DTO) // 상수 사용
            .build();
    }
}

