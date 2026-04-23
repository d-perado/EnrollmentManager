package org.example.enrollmentmanager.dto.enrollment;

import org.example.enrollmentmanager.domain.enrollment.Enrollment;
import org.example.enrollmentmanager.domain.enrollment.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long userId,
        Long courseId,
        EnrollmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime confirmedAt
) {
    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getUser().getId(),
                enrollment.getCourse().getId(),
                enrollment.getStatus(),
                enrollment.getCreatedAt(),
                enrollment.getConfirmedAt()
        );
    }
}