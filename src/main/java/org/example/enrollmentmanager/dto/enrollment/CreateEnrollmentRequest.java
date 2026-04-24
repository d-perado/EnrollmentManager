package org.example.enrollmentmanager.dto.enrollment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "수강 신청 요청 DTO")
public record CreateEnrollmentRequest(

        @Schema(description = "사용자 회원 ID", example = "1")
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @Schema(description = "강의 ID", example = "3")
        @NotNull(message = "강의 ID는 필수입니다.")
        Long courseId

) {
}