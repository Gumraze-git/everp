package org.ever._4ever_be_gw.common.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
    ElementType.PARAMETER,   // @PathVariable, @RequestParam
    ElementType.FIELD,       // DTO 필드
    ElementType.TYPE_USE     // List<@ValidUuidV7 String> 같은 제네릭 타입 내부
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UuidV7Validator.class)
public @interface ValidUuidV7 {

    String message() default "유효하지 않은 ID 형식입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}