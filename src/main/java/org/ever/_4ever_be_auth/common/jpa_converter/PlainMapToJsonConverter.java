package org.ever._4ever_be_auth.common.jpa_converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Converter(autoApply = false)
@Component
@RequiredArgsConstructor
public class PlainMapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    // 플레인 매퍼 주입 (SecurityJacksonConfig에서 제공)
    private final @Qualifier("plainJsonObjectMapper") ObjectMapper mapper;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}"; // 정책상 빈 JSON 저장
        }
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("Map → JSON 직렬화 실패", e);
            throw new IllegalArgumentException("Map을 JSON으로 직렬화하는 데 실패했습니다.", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            ObjectMapper safe = mapper.copy();
            try {
                safe.deactivateDefaultTyping();
            } catch (NoSuchMethodError | Exception ignore) {}
            return safe.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("JSON → Map 역직렬화 실패", e);
            throw new IllegalArgumentException("JSON을 Map으로 역직렬화하는 데 실패했습니다.", e);
        }
    }

}
