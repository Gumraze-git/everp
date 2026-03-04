package org.ever._4ever_be_alarm.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = AllowedValuesValidator.class) // 2. 어노테이션의 로직을 담당할 Validator 클래스
@Target({ // 1. 어노테이션을 어디에 붙일 수 있는지 지정
    ElementType.PARAMETER,  // @RequestParam, @PathVariable 등에 붙일 수 있음
    ElementType.FIELD,      // DTO 필드 등에 붙일 수 있음
    ElementType.METHOD      // 메서드 반환값 등에 붙일 수 있음
})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedValues {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "허용되지 않는 값입니다."; // 3. 유효성 검사 실패 시 반환할 메시지

    String[] allowedValues(); // 4. 허용할 값의 목록 (예: {"asc", "desc"})

    boolean ignoreCase() default false; // 5. 대소문자 구분 여부
}
