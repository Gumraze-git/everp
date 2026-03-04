package org.ever._4ever_be_gw.business.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 견적 상태 코드 상수.
 * 기존 String[] codes = {"PENDING", "REVIEW", "APPROVAL", "REJECTED"} 대체용.
 */
public enum QuotationStatus {
    PENDING("PENDING"),
    REVIEW("REVIEW"),
    APPROVAL("APPROVAL"),
    REJECTED("REJECTED");

    private final String code;

    QuotationStatus(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static QuotationStatus from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (QuotationStatus s : values()) {
            if (s.code.equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown QuotationStatus: " + value);
    }
}

