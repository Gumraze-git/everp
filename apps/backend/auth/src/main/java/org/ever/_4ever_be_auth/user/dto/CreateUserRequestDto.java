package org.ever._4ever_be_auth.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_auth.user.enums.UserRole;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDto {
    @Email
    @NotBlank
    private String contactEmail;

    @NotNull
    private UserRole userRole;
}
