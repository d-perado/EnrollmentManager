package org.example.enrollmentmanager.service;

import org.example.enrollmentmanager.common.exception.BusinessException;
import org.example.enrollmentmanager.common.exception.ErrorCode;
import org.example.enrollmentmanager.domain.course.Course;
import org.example.enrollmentmanager.domain.enrollment.EnrollmentStatus;
import org.example.enrollmentmanager.domain.user.User;
import org.example.enrollmentmanager.domain.user.UserRole;
import org.example.enrollmentmanager.dto.enrollment.CreateEnrollmentRequest;
import org.example.enrollmentmanager.dto.enrollment.EnrollmentResponse;
import org.example.enrollmentmanager.repository.CourseRepository;
import org.example.enrollmentmanager.repository.EnrollmentRepository;
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
class EnrollmentServiceTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("OPEN 상태의 강의에 수강 신청하면 PENDING 상태의 신청이 생성된다")
    void createEnrollment_success() {
        User student = saveUser("student@test.com", UserRole.STUDENT);
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        Course course = saveOpenCourse(instructor, 10);

        EnrollmentResponse response = enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student.getId(), course.getId())
        );

        assertThat(response.id()).isNotNull();
        assertThat(response.userId()).isEqualTo(student.getId());
        assertThat(response.courseId()).isEqualTo(course.getId());
        assertThat(response.status()).isEqualTo(EnrollmentStatus.PENDING);
    }

    @Test
    @DisplayName("같은 유저가 같은 강의에 중복 신청하면 실패한다")
    void createEnrollment_duplicate_fail() {
        User student = saveUser("student@test.com", UserRole.STUDENT);
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        Course course = saveOpenCourse(instructor, 10);

        enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student.getId(), course.getId())
        );

        assertThatThrownBy(() -> enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student.getId(), course.getId())
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATE_ENROLLMENT.getMessage());
    }

    @Test
    @DisplayName("결제 확정 시 Enrollment는 CONFIRMED가 되고 강의 확정 인원이 증가한다")
    void confirmEnrollment_success() {
        User student = saveUser("student@test.com", UserRole.STUDENT);
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        Course course = saveOpenCourse(instructor, 10);

        EnrollmentResponse created = enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student.getId(), course.getId())
        );

        EnrollmentResponse confirmed = enrollmentService.confirmEnrollment(created.id());

        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();

        assertThat(confirmed.status()).isEqualTo(EnrollmentStatus.CONFIRMED);
        assertThat(confirmed.confirmedAt()).isNotNull();
        assertThat(updatedCourse.getConfirmedCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("정원이 가득 찬 강의는 결제 확정에 실패한다")
    void confirmEnrollment_capacityExceeded_fail() {
        User student1 = saveUser("student1@test.com", UserRole.STUDENT);
        User student2 = saveUser("student2@test.com", UserRole.STUDENT);
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        Course course = saveOpenCourse(instructor, 1);

        EnrollmentResponse first = enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student1.getId(), course.getId())
        );
        enrollmentService.confirmEnrollment(first.id());

        EnrollmentResponse second = enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student2.getId(), course.getId())
        );

        assertThatThrownBy(() -> enrollmentService.confirmEnrollment(second.id()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COURSE_CAPACITY_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("결제 확정된 수강 신청은 7일 이내에 취소할 수 있다")
    void cancelEnrollment_success() {
        User student = saveUser("student@test.com", UserRole.STUDENT);
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        Course course = saveOpenCourse(instructor, 10);

        EnrollmentResponse created = enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student.getId(), course.getId())
        );
        EnrollmentResponse confirmed = enrollmentService.confirmEnrollment(created.id());

        EnrollmentResponse cancelled = enrollmentService.cancelEnrollment(confirmed.id());

        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();

        assertThat(cancelled.status()).isEqualTo(EnrollmentStatus.CANCELLED);
        assertThat(updatedCourse.getConfirmedCount()).isEqualTo(0);
    }

    private User saveUser(String email, UserRole role) {
        User user = User.builder()
                .email(email)
                .name("test-user")
                .role(role)
                .build();

        return userRepository.save(user);
    }

    private Course saveOpenCourse(User instructor, int capacity) {
        Course course = Course.create(
                instructor,
                "테스트 강의",
                "테스트 설명",
                10000,
                capacity,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(30)
        );

        course.open();

        return courseRepository.save(course);
    }

    @Test
    @DisplayName("정원이 가득 찬 강의에 신청하면 WAITLIST 상태가 된다")
    void createEnrollment_waitlist_success() {
        User student1 = saveUser("student1@test.com", UserRole.STUDENT);
        User student2 = saveUser("student2@test.com", UserRole.STUDENT);
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        Course course = saveOpenCourse(instructor, 1);

        EnrollmentResponse first = enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student1.getId(), course.getId())
        );
        enrollmentService.confirmEnrollment(first.id());

        EnrollmentResponse waitlisted = enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student2.getId(), course.getId())
        );

        assertThat(waitlisted.status()).isEqualTo(EnrollmentStatus.WAITLIST);
    }

    @Test
    @DisplayName("수강 취소로 자리가 생기면 가장 오래된 WAITLIST 신청이 PENDING으로 승격된다")
    void cancelEnrollment_promoteWaitlist_success() {
        User student1 = saveUser("student1@test.com", UserRole.STUDENT);
        User student2 = saveUser("student2@test.com", UserRole.STUDENT);
        User instructor = saveUser("instructor@test.com", UserRole.INSTRUCTOR);
        Course course = saveOpenCourse(instructor, 1);

        EnrollmentResponse first = enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student1.getId(), course.getId())
        );
        enrollmentService.confirmEnrollment(first.id());

        EnrollmentResponse waitlisted = enrollmentService.createEnrollment(
                new CreateEnrollmentRequest(student2.getId(), course.getId())
        );

        enrollmentService.cancelEnrollment(first.id());

        Enrollment promoted = enrollmentRepository.findById(waitlisted.id()).orElseThrow();

        assertThat(promoted.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
    }
}