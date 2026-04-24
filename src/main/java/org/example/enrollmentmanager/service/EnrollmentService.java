package org.example.enrollmentmanager.service;

import lombok.RequiredArgsConstructor;
import org.example.enrollmentmanager.common.exception.BusinessException;
import org.example.enrollmentmanager.common.exception.ErrorCode;
import org.example.enrollmentmanager.domain.course.Course;
import org.example.enrollmentmanager.domain.enrollment.Enrollment;
import org.example.enrollmentmanager.domain.enrollment.EnrollmentStatus;
import org.example.enrollmentmanager.domain.user.User;
import org.example.enrollmentmanager.domain.user.UserRole;
import org.example.enrollmentmanager.dto.enrollment.CreateEnrollmentRequest;
import org.example.enrollmentmanager.dto.enrollment.EnrollmentResponse;
import org.example.enrollmentmanager.repository.EnrollmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseService courseService;

    @Transactional
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest request) {
        User user = userService.findUserById(request.userId());
        Course course = courseService.findCourseById(request.courseId());

        validateStudent(user);
        validateCourseOpen(course);
        validateDuplicateEnrollment(user.getId(), course.getId());

        Enrollment enrollment = Enrollment.create(user, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return EnrollmentResponse.from(savedEnrollment);
    }

    public Page<EnrollmentResponse> getMyEnrollments(Long userId, Pageable pageable) {
        userService.findUserById(userId);

        Page<Enrollment> enrollments = enrollmentRepository.findAllByUserId(userId, pageable);

        return enrollments.map(EnrollmentResponse::from);
    }

    public Page<EnrollmentResponse> getCourseEnrollments(
            Long instructorId,
            Long courseId,
            Pageable pageable
    ) {
        User instructor = userService.findUserById(instructorId);
        Course course = courseService.findCourseById(courseId);

        validateInstructor(instructor);
        validateCourseOwner(instructor, course);

        Page<Enrollment> enrollments =
                enrollmentRepository.findAllByCourseIdAndStatus(
                        courseId,
                        EnrollmentStatus.CONFIRMED,
                        pageable
                );

        return enrollments.map(EnrollmentResponse::from);
    }

    @Transactional
    public EnrollmentResponse confirmEnrollment(Long enrollmentId) {
        Enrollment enrollment = findEnrollmentById(enrollmentId);

        Course course = courseService.findCourseByIdWithLock(
                enrollment.getCourse().getId()
        );

        if (course.isFull()) {
            throw new BusinessException(ErrorCode.COURSE_CAPACITY_EXCEEDED);
        }

        enrollment.confirm();
        course.increaseConfirmedCount();

        return EnrollmentResponse.from(enrollment);
    }

    @Transactional
    public EnrollmentResponse cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = findEnrollmentById(enrollmentId);

        Course course = courseService.findCourseByIdWithLock(
                enrollment.getCourse().getId()
        );

        enrollment.cancel();
        course.decreaseConfirmedCount();

        promoteWaitlist(course);

        return EnrollmentResponse.from(enrollment);
    }

    public Enrollment findEnrollmentById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));
    }

    private void promoteWaitlist(Course course) {
        enrollmentRepository
                .findFirstByCourseIdAndStatusOrderByCreatedAtAsc(
                        course.getId(),
                        EnrollmentStatus.WAITLIST
                )
                .ifPresent(Enrollment::promoteToPending);
    }

    private void validateCourseOpen(Course course) {
        if (!course.isOpen()) {
            throw new BusinessException(ErrorCode.COURSE_NOT_OPEN);
        }
    }

    private void validateDuplicateEnrollment(Long userId, Long courseId) {
        boolean exists = enrollmentRepository.existsByUserIdAndCourseIdAndStatusIn(
                userId,
                courseId,
                List.of(
                        EnrollmentStatus.WAITLIST,
                        EnrollmentStatus.PENDING,
                        EnrollmentStatus.CONFIRMED
                )
        );

        if (exists) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENROLLMENT);
        }
    }

    private void validateStudent(User user) {
        if (user.getRole() != UserRole.STUDENT) {
            throw new BusinessException(ErrorCode.INVALID_USER_ROLE, "수강생만 수강 신청할 수 있습니다.");
        }
    }

    private void validateInstructor(User user) {
        if (user.getRole() != UserRole.INSTRUCTOR) {
            throw new BusinessException(ErrorCode.INVALID_USER_ROLE, "강사만 수강생 목록을 조회할 수 있습니다.");
        }
    }

    private void validateCourseOwner(User instructor, Course course) {
        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new BusinessException(ErrorCode.INVALID_USER_ROLE, "해당 강의의 강사가 아닙니다.");
        }
    }
}