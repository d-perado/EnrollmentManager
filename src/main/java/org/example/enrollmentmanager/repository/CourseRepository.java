package org.example.enrollmentmanager.repository;

import org.example.enrollmentmanager.domain.course.Course;
import org.example.enrollmentmanager.domain.course.CourseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByStatus(CourseStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Course> findWithLockById(Long id);
}