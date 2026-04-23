package org.example.enrollmentmanager.service;

import org.example.enrollmentmanager.common.exception.BusinessException;
import org.example.enrollmentmanager.common.exception.ErrorCode;
import org.example.enrollmentmanager.domain.user.UserRole;
import org.example.enrollmentmanager.dto.user.CreateUserRequest;
import org.example.enrollmentmanager.dto.user.UserResponse;
import org.example.enrollmentmanager.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자를 생성할 수 있다")
    void createUser_success() {
        CreateUserRequest request = new CreateUserRequest(
                "student@test.com",
                "student",
                UserRole.STUDENT
        );

        UserResponse response = userService.createUser(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo("student@test.com");
        assertThat(response.name()).isEqualTo("student");
        assertThat(response.role()).isEqualTo(UserRole.STUDENT);
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 사용자를 생성할 수 없다")
    void createUser_duplicateEmail_fail() {
        CreateUserRequest request = new CreateUserRequest(
                "student@test.com",
                "student",
                UserRole.STUDENT
        );

        userService.createUser(request);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("사용자 ID로 사용자를 조회할 수 있다")
    void getUser_success() {
        CreateUserRequest request = new CreateUserRequest(
                "instructor@test.com",
                "instructor",
                UserRole.INSTRUCTOR
        );

        UserResponse createdUser = userService.createUser(request);

        UserResponse response = userService.getUser(createdUser.id());

        assertThat(response.id()).isEqualTo(createdUser.id());
        assertThat(response.email()).isEqualTo("instructor@test.com");
        assertThat(response.role()).isEqualTo(UserRole.INSTRUCTOR);
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 조회하면 실패한다")
    void getUser_notFound_fail() {
        Long notFoundUserId = 999L;

        assertThatThrownBy(() -> userService.getUser(notFoundUserId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}