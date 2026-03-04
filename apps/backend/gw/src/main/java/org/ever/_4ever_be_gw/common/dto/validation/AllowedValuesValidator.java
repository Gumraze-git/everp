package org.ever._4ever_be_gw.common.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, String> {

    private Set<String> allowedValues;
    private boolean ignoreCase;

    @Override
    public void initialize(AllowedValues constraintAnnotation) {
        // 어노테이션의 속성(allowedValues, ignoreCase)을 가져와 초기화합니다.
        this.ignoreCase = constraintAnnotation.ignoreCase();

        // Set으로 만들어 검색 속도를 높입니다.
        this.allowedValues = new HashSet<>(constraintAnnotation.allowedValues().length);
        for (String value : constraintAnnotation.allowedValues()) {
            this.allowedValues.add(ignoreCase ? value.toLowerCase() : value);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // @RequestParam(required = false)인 경우 null이나 빈 값이 들어올 수 있습니다.
        // 이 경우 유효성 검사를 통과시킵니다. (필수 값 검사는 @NotBlank 등으로 별도 처리)
        if (value == null || value.isBlank()) {
            return true;
        }

        // 대소문자를 구분하지 않는다면, 입력값도 소문자로 변경하여 비교합니다.
        String valueToCompare = ignoreCase ? value.toLowerCase() : value;

        // 허용된 값 목록(Set)에 포함되어 있는지 확인합니다.
        return allowedValues.contains(valueToCompare);
    }
}