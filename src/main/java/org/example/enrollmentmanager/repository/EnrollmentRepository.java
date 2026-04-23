package org.example.enrollmentmanager.repository;

import org.example.enrollmentmanager.domain.enrollment.Enrollment;
import org.example.enrollmentmanager.domain.enrollment.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserIdAndCourseIdAndStatusIn(
            Long userId,
            Long courseId,
            List<EnrollmentStatus> statuses
    );

    List<Enrollment> findAllByUserId(Long userId);
}