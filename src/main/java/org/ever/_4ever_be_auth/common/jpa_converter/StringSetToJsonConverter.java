package org.ever._4ever_be_auth.common.jpa_converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Set<String> <-> JSON(String) 컨버터
 * <p>
 * - 전역 ObjectMapper(스프링 빈) 주입: SecurityJacksonConfig에서 등록된 모듈과 동일 설정 공유
 * - 입력이 null/빈 집합이면 DB에는 null을 저장
 * - 역직렬화 시 순서 보존을 위해 LinkedHashSet 사용
 */
@Slf4j
@Converter(autoApply = true)
@Component
@RequiredArgsConstructor
public class StringSetToJsonConverter implements AttributeConverter<Set<String>, String> {

    private final ObjectMapper objectMapper; // 전역 매퍼 주입

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            // JSON(JavaScript Object Notation) 배열로 저장
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("Set<String>을 JSON으로 직렬화하는 데 실패했습니다.", e);
            throw new IllegalArgumentException("스코프 집합을 직렬화하는 데 실패했습니다.", e);
        }
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptySet();
        }
        try {
            // JSON 배열 -> LinkedHashSet<String> (순서 보존)
            LinkedHashSet<String> result =
                    objectMapper.readValue(dbData, new TypeReference<LinkedHashSet<String>>() {
                    });
            return result != null ? result : Collections.emptySet();
        } catch (Exception e) {
            log.error("JSON을 Set<String>으로 역직렬화하는 데 실패했습니다.", e);
            throw new IllegalArgumentException("스코프 집합을 역직렬화하는 데 실패했습니다.", e);
        }
    }
}