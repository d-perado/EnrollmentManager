package dto.user;

import domain.user.User;
import domain.user.UserRole;

public record UserResponse(
        Long id,
        String email,
        String name,
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