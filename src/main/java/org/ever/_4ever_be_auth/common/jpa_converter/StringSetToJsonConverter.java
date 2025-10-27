package org.ever._4ever_be_auth.common.jpa_converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Converter(autoApply = true)
public class StringSetToJsonConverter implements AttributeConverter<Set<String>, String> {

    // 전역 설정 영향 안 받는 로컬 ObjectMapper (default typing 미사용)
    private static final ObjectMapper M = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    private static final TypeReference<Set<String>> SET_OF_STRING = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) return "[]";
            // 항상 순수 배열로 저장되도록 불변/언모디파이어블 Set을 중립화
            return M.writeValueAsString(List.copyOf(attribute));
        } catch (Exception e) {
            log.error("Set<String>을 JSON으로 직렬화하는 데 실패했습니다.", e);
            throw new IllegalArgumentException("스코프 집합을 직렬화하는 데 실패했습니다.", e);
        }
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) return Set.of();

            // 과거 폴리모픽 WRAPPER_ARRAY 저장분이면 벗겨서 순수 배열로 바꿔줌
            String normalized = unwrapIfPolymorphicArray(dbData);

            Set<String> raw = M.readValue(normalized, SET_OF_STRING);
            return (raw == null) ? Set.of() : new LinkedHashSet<>(raw); // 순서 보존
        } catch (Exception e) {
            log.error("JSON을 Set<String>으로 역직렬화하는 데 실패했습니다.", e);
            throw new IllegalArgumentException("스코프 집합을 역직렬화하는 데 실패했습니다.", e);
        }
    }

    // ["java.util.Collections$UnmodifiableSet", ["a","b"]] -> ["a","b"]
    private String unwrapIfPolymorphicArray(String json) throws Exception {
        if (json.length() > 2 && json.charAt(0) == '[' && json.contains("java.util.")) {
            var node = M.readTree(json);
            if (node.isArray() && node.size() == 2 && node.get(1).isArray()) {
                return M.writeValueAsString(node.get(1));
            }
        }
        return json;
    }
}
