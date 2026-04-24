package org.example.enrollmentmanager.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.enrollmentmanager.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원 생성 요청 DTO")
public record CreateUserRequest(

        @Schema(description = "이메일", example = "user@example.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "이름", example = "홍길동")
        @NotBlank
        String name,

        @Schema(description = "회원 역할", example = "INSTRUCTOR")
        @NotNull
        UserRole role

) {
}