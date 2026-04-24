package org.example.enrollmentmanager.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "강의 생성 요청 DTO")
public record CreateCourseRequest(

        @Schema(description = "강사 회원 ID", example = "1")
        @NotNull(message = "강사 ID는 필수입니다.")
        Long instructorId,

        @Schema(description = "강의 제목", example = "Spring Boot 실전 입문")
        @NotBlank(message = "강의 제목은 필수입니다.")
        String title,

        @Schema(description = "강의 설명", example = "Spring Boot 기반 백엔드 서비스 개발 강의입니다.")
        @NotBlank(message = "강의 설명은 필수입니다.")
        String description,

        @Schema(description = "수강료", example = "150000")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        int price,

        @Schema(description = "정원", example = "30")
        @Min(value = 1, message = "정원은 1 이상이어야 합니다.")
        int capacity,

        @Schema(description = "강의 시작일", example = "2026-05-10T09:00:00")
        @NotNull(message = "수강 시작일은 필수입니다.")
        LocalDateTime startDate,

        @Schema(description = "강의 종료일", example = "2026-06-10T18:00:00")
        @NotNull(message = "수강 종료일은 필수입니다.")
        LocalDateTime endDate

) {
}