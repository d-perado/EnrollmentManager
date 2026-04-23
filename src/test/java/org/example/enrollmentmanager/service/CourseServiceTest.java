package org.example.enrollmentmanager.service;

import org.example.enrollmentmanager.common.exception.BusinessException;
import org.example.enrollmentmanager.common.exception.ErrorCode;
import org.example.enrollmentmanager.domain.course.CourseStatus;
import org.example.enrollmentmanager.domain.user.User;
import org.example.enrollmentmanager.domain.user.UserRole;
import org.example.enrollmentmanager.dto.course.CourseResponse;
import org.example.enrollmentmanager.dto.course.CreateCourseRequest;
import org.example.enrollmentmanager.repository.CourseRepository;
import org.example.enrollmentmanager.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @DisplayName("강사는 강의를 생성할 수 있다")
    void createCourse_success() {
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);

        CreateCourseRequest request = new CreateCourseRequest(
                instructor.getId(),
                "테스트 강의",
                "테스트 설명",
                10000,
                10,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(30)
        );

        CourseResponse response = courseService.createCourse(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.instructorId()).isEqualTo(instructor.getId());
        assertThat(response.title()).isEqualTo("테스트 강의");
        assertThat(response.status()).isEqualTo(CourseStatus.DRAFT);
        assertThat(response.confirmedCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("수강생은 강의를 생성할 수 없다")
    void createCourse_student_fail() {
        User student = saveUser("student@test.com", UserRole.STUDENT);

        CreateCourseRequest request = new CreateCourseRequest(
                student.getId(),
                "테스트 강의",
                "테스트 설명",
                10000,
                10,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(30)
        );

        assertThatThrownBy(() -> courseService.createCourse(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_USER_ROLE.getMessage());
    }

    @Test
    @DisplayName("DRAFT 상태의 강의는 OPEN 상태로 변경할 수 있다")
    void openCourse_success() {
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        CourseResponse course = createCourse(instructor);

        CourseResponse openedCourse = courseService.openCourse(course.id());

        assertThat(openedCourse.status()).isEqualTo(CourseStatus.OPEN);
    }

    @Test
    @DisplayName("OPEN 상태의 강의는 CLOSED 상태로 변경할 수 있다")
    void closeCourse_success() {
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        CourseResponse course = createCourse(instructor);

        courseService.openCourse(course.id());
        CourseResponse closedCourse = courseService.closeCourse(course.id());

        assertThat(closedCourse.status()).isEqualTo(CourseStatus.CLOSED);
    }

    @Test
    @DisplayName("DRAFT 상태의 강의는 바로 CLOSED 상태로 변경할 수 없다")
    void closeCourse_invalidStatus_fail() {
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        CourseResponse course = createCourse(instructor);

        assertThatThrownBy(() -> courseService.closeCourse(course.id()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("OPEN 상태에서만 CLOSED");
    }

    @Test
    @DisplayName("강의 목록은 상태 기준으로 필터링할 수 있다")
    void getCourses_withStatusFilter_success() {
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);

        CourseResponse draftCourse = createCourse(instructor);
        CourseResponse openCourse = createCourse(instructor);
        courseService.openCourse(openCourse.id());

        var openCourses = courseService.getCourses(CourseStatus.OPEN);

        assertThat(openCourses).hasSize(1);
        assertThat(openCourses.get(0).id()).isEqualTo(openCourse.id());
        assertThat(openCourses.get(0).status()).isEqualTo(CourseStatus.OPEN);
    }

    private User saveUser(String email, UserRole role) {
        User user = User.builder()
                .email(email)
                .name("test-user")
                .role(role)
                .build();

        return userRepository.save(user);
    }

    private CourseResponse createCourse(User instructor) {
        CreateCourseRequest request = new CreateCourseRequest(
                instructor.getId(),
                "테스트 강의",
                "테스트 설명",
                10000,
                10,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(30)
        );

        return courseService.createCourse(request);
    }
}