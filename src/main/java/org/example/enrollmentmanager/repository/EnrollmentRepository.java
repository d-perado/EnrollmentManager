package org.example.enrollmentmanager.repository;

import org.example.enrollmentmanager.domain.enrollment.Enrollment;
import org.example.enrollmentmanager.domain.enrollment.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByUserIdAndCourseIdAndStatusIn(
            Long userId,
            Long courseId,
            List<EnrollmentStatus> statuses
    );

    Page<Enrollment> findAllByUserId(Long userId, Pageable pageable);

    Page<Enrollment> findAllByCourseIdAndStatus(
            Long courseId,
            EnrollmentStatus status,
            Pageable pageable
    );

    Optional<Enrollment> findFirstByCourseIdAndStatusOrderByCreatedAtAsc(
            Long courseId,
            EnrollmentStatus status
    );
}