package org.example.enrollmentmanager.dto.course;

import org.example.enrollmentmanager.domain.course.Course;
import org.example.enrollmentmanager.domain.course.CourseStatus;

import java.time.LocalDateTime;

public record CourseResponse(
        Long id,
        Long instructorId,
        String title,
        String description,
        int price,
        int capacity,
        int confirmedCount,
        CourseStatus status,
        LocalDateTime startDate,
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