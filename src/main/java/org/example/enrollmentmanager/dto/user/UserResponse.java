package org.example.enrollmentmanager.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.enrollmentmanager.domain.user.User;
import org.example.enrollmentmanager.domain.user.UserRole;

@Schema(description = "회원 응답 DTO")
public record UserResponse(

        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "회원 역할", example = "INSTRUCTOR")
        UserRole role

) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}