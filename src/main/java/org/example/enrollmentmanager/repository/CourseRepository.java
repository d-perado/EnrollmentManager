package org.example.enrollmentmanager.repository;

import org.example.enrollmentmanager.domain.course.Course;
import org.example.enrollmentmanager.domain.course.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByStatus(CourseStatus status);
}