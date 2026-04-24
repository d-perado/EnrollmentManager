package org.example.enrollmentmanager.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.enrollmentmanager.domain.course.Course;
import org.example.enrollmentmanager.domain.course.CourseStatus;

import java.time.LocalDateTime;

@Schema(description = "강의 응답 DTO")
public record CourseResponse(

        @Schema(description = "강의 ID", example = "1")
        Long id,

        @Schema(description = "강사 회원 ID", example = "1")
        Long instructorId,

        @Schema(description = "강의명", example = "Spring Boot 실전 입문")
        String title,

        @Schema(description = "강의 설명", example = "Spring Boot 기반 백엔드 서비스 개발 강의입니다.")
        String description,

        @Schema(description = "수강료", example = "150000")
        int price,

        @Schema(description = "정원", example = "30")
        int capacity,

        @Schema(description = "확정 수강 인원", example = "18")
        int confirmedCount,

        @Schema(description = "강의 상태", example = "OPEN")
        CourseStatus status,

        @Schema(description = "강의 시작일", example = "2026-05-10T09:00:00")
        LocalDateTime startDate,

        @Schema(description = "강의 종료일", example = "2026-06-10T18:00:00")
        LocalDateTime endDate

) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getInstructor().getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getCapacity(),
                course.getConfirmedCount(),
                course.getStatus(),
                course.getStartDate(),
                course.getEndDate()
        );
    }
}