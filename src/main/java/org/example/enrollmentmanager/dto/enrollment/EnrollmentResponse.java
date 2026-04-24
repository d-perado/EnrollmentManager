package org.example.enrollmentmanager.dto.enrollment;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.enrollmentmanager.domain.enrollment.Enrollment;
import org.example.enrollmentmanager.domain.enrollment.EnrollmentStatus;

import java.time.LocalDateTime;

@Schema(description = "수강 신청 응답 DTO")
public record EnrollmentResponse(

        @Schema(description = "수강 신청 ID", example = "100")
        Long id,

        @Schema(description = "사용자 회원 ID", example = "1")
        Long userId,

        @Schema(description = "강의 ID", example = "1")
        Long courseId,

        @Schema(description = "수강 상태", example = "WAITLIST")
        EnrollmentStatus status,

        @Schema(description = "신청 일시", example = "2026-04-24T14:30:00")
        LocalDateTime createdAt,

        @Schema(description = "확정 일시", example = "2026-04-24T14:35:00")
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