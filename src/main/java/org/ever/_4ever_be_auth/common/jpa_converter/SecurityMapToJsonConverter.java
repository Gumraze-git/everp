package org.ever._4ever_be_auth.common.jpa_converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Converter(autoApply = false)
@Component
@RequiredArgsConstructor
public class SecurityMapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private final @Qualifier("securityJsonObjectMapper") ObjectMapper mapper;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return mapper.writeValueAsString(attribute == null ? Map.of() : attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Security Map → JSON 직렬화 실패", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return new LinkedHashMap<>();
        try {
            return mapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Security JSON → Map 역직렬화 실패", e);
        }
    }
}
