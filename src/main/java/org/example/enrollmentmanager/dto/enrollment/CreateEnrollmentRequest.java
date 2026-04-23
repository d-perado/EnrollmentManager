package org.example.enrollmentmanager.dto.enrollment;

import jakarta.validation.constraints.NotNull;

public record CreateEnrollmentRequest(
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @NotNull(message = "강의 ID는 필수입니다.")
        Long courseId
) {
}