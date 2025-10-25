package org.ever._4ever_be_auth.common.jpa_converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Converter(autoApply = false)
public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        // attribute가 null이거나 비어 있어면, 데이터베이스에 아무 것도 저장하지 않음
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("클라이언트 설정을 직렬화하는 데 실패했습니다.", e);
            throw new IllegalArgumentException("클라이언트 설정을 직렬화하는 데 실패했습니다.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            return OBJECT_MAPPER.readValue(dbData, Map.class);
        } catch (IOException e) {
            log.error("클라이언트 설정을 역직렬화하는 데 실패했습니다.", e);
            throw new IllegalArgumentException("클라이언트 설정을 역직렬화하는 데 실패했습니다.", e);
        }
    }
}
