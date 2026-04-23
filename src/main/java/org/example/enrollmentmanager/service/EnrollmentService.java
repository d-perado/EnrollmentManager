package org.example.enrollmentmanager.service;

import org.example.enrollmentmanager.common.exception.BusinessException;
import org.example.enrollmentmanager.common.exception.ErrorCode;
import org.example.enrollmentmanager.domain.course.Course;
import org.example.enrollmentmanager.domain.enrollment.Enrollment;
import org.example.enrollmentmanager.domain.enrollment.EnrollmentStatus;
import org.example.enrollmentmanager.domain.user.User;
import org.example.enrollmentmanager.dto.enrollment.CreateEnrollmentRequest;
import org.example.enrollmentmanager.dto.enrollment.EnrollmentResponse;
import org.example.enrollmentmanager.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
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

        validateCourseOpen(course);
        validateDuplicateEnrollment(user.getId(), course.getId());

        Enrollment enrollment = Enrollment.create(user, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return EnrollmentResponse.from(savedEnrollment);
    }

    public List<EnrollmentResponse> getMyEnrollments(Long userId) {
        userService.findUserById(userId);

        return enrollmentRepository.findAllByUserId(userId)
                .stream()
                .map(EnrollmentResponse::from)
                .toList();
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

    public Enrollment findEnrollmentById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));
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
                List.of(EnrollmentStatus.PENDING, EnrollmentStatus.CONFIRMED)
        );

        if (exists) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENROLLMENT);
        }
    }
}