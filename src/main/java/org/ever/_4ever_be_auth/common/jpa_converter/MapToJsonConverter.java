package org.ever._4ever_be_auth.common.jpa_converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Map<String, Object> <-> JSON(String)
 * <p>
 * - 전역 ObjectMapper(Spring Bean) 사용
 * - SecurityJacksonConfig 에서 등록된 SecurityJackson2Modules 모듈 공유
 * - Jackson Allowlist 문제 방지
 */
@Slf4j
@Converter(autoApply = false)
@Component
@RequiredArgsConstructor
public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("Map을 JSON으로 직렬화하는 데 실패했습니다.", e);
            throw new IllegalArgumentException("Map을 JSON으로 직렬화하는 데 실패했습니다.", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("JSON을 Map으로 역직렬화하는 데 실패했습니다.", e);
            throw new IllegalArgumentException("JSON을 Map으로 역직렬화하는 데 실패했습니다.", e);
        }
    }
}