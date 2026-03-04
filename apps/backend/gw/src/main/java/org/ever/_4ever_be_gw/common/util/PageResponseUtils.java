package org.ever._4ever_be_gw.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PageResponseUtils {

    private PageResponseUtils() {
    }

    public static Map<String, Object> buildPage(int number, int size, long totalElements) {
        int safeSize = size <= 0 ? 1 : size;
        int safeNumber = Math.max(number, 0);
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
}

