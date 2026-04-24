package org.example.enrollmentmanager.domain.enrollment;

import org.example.enrollmentmanager.common.exception.BusinessException;
import org.example.enrollmentmanager.common.exception.ErrorCode;
import org.example.enrollmentmanager.domain.course.Course;
import org.example.enrollmentmanager.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "course_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;

    // ===== 비즈니스 로직 =====

    public static Enrollment create(User user, Course course) {
        EnrollmentStatus initialStatus = course.isFull()
                ? EnrollmentStatus.WAITLIST
                : EnrollmentStatus.PENDING;

        return Enrollment.builder()
                .user(user)
                .course(course)
                .status(initialStatus)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void confirm() {
        if (this.status != EnrollmentStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_ENROLLMENT_STATUS);
        }

        if (isPendingExpired()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_PENDING_EXPIRED);
        }

        this.status = EnrollmentStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void promoteToPending() {
        if (this.status != EnrollmentStatus.WAITLIST) {
            throw new BusinessException(ErrorCode.INVALID_ENROLLMENT_STATUS);
        }

        this.status = EnrollmentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void cancel() {
        if (!isCancelable()) {
            throw new BusinessException(ErrorCode.ENROLLMENT_NOT_CANCELABLE);
        }

        this.status = EnrollmentStatus.CANCELLED;
    }

    public boolean isPendingExpired() {
        return createdAt.plusMinutes(30).isBefore(LocalDateTime.now());
    }

    public boolean isCancelable() {
        if (status != EnrollmentStatus.CONFIRMED) return false;
        return confirmedAt.plusDays(7).isAfter(LocalDateTime.now());
    }

    public boolean isPending() {
        return this.status == EnrollmentStatus.PENDING;
    }

    public boolean isConfirmed() {
        return this.status == EnrollmentStatus.CONFIRMED;
    }
}