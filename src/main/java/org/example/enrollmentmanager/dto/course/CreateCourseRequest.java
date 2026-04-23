package org.example.enrollmentmanager.dto.course;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateCourseRequest(

        @NotNull(message = "강사 ID는 필수입니다.")
        Long instructorId,

        @NotBlank(message = "강의 제목은 필수입니다.")
        String title,

        @NotBlank(message = "강의 설명은 필수입니다.")
        String description,

        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        int price,

        @Min(value = 1, message = "정원은 1 이상이어야 합니다.")
        int capacity,

        @NotNull(message = "수강 시작일은 필수입니다.")
        LocalDateTime startDate,

        @NotNull(message = "수강 종료일은 필수입니다.")
        LocalDateTime endDate
) {
}