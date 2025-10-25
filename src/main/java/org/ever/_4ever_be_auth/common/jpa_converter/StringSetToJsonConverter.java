package org.ever._4ever_be_auth.common.jpa_converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Converter(autoApply = true)
public class StringSetToJsonConverter implements AttributeConverter<Set<String>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Set을 직렬화 하는데 실패했습니다.", e);
            throw new IllegalArgumentException("스코프 집합을 직렬화 하는데 실패했습니다.", e);
        }
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptySet();
        }

        try {
            String[] values = OBJECT_MAPPER.readValue(dbData, String[].class);
            Set<String> result = new LinkedHashSet<>();
            Collections.addAll(result, values);
            return result;
        } catch (IOException e) {
            log.error("스코프 집합을 역직렬화 하는데 실패했습니다.", e);
            throw new IllegalArgumentException("스코프 집합을 역직렬화 하는데 실패했습니다.", e);
        }
    }
}
