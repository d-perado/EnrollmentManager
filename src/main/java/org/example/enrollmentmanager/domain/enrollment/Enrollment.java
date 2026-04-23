package org.example.enrollmentmanager.domain.enrollment;

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
        return Enrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void confirm() {
        if (this.status != EnrollmentStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 확정 가능합니다.");
        }

        if (isPendingExpired()) {
            throw new IllegalStateException("결제 가능 시간이 만료되었습니다.");
        }

        this.status = EnrollmentStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (!isCancelable()) {
            throw new IllegalStateException("취소할 수 없는 상태입니다.");
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