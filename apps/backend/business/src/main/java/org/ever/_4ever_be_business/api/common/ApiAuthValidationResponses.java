package org.ever._4ever_be_business.api.common;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {})
@ApiBadRequestResponse
@ApiUnauthorizedResponse
@ApiForbiddenResponse
public @interface ApiAuthValidationResponses {
}
