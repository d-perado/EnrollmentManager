package org.example.enrollmentmanager.service;

import org.example.enrollmentmanager.common.exception.BusinessException;
import org.example.enrollmentmanager.common.exception.ErrorCode;
import org.example.enrollmentmanager.domain.course.Course;
import org.example.enrollmentmanager.domain.course.CourseStatus;
import org.example.enrollmentmanager.domain.user.User;
import org.example.enrollmentmanager.domain.user.UserRole;
import org.example.enrollmentmanager.dto.course.CourseResponse;
import org.example.enrollmentmanager.dto.course.CreateCourseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.enrollmentmanager.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        User instructor = userService.findUserById(request.instructorId());
        validateInstructor(instructor);

        Course course = Course.create(
                instructor,
                request.title(),
                request.description(),
                request.price(),
                request.capacity(),
                request.startDate(),
                request.endDate()
        );

        Course savedCourse = courseRepository.save(course);
        return CourseResponse.from(savedCourse);
    }

    public List<CourseResponse> getCourses(CourseStatus status) {
        List<Course> courses = (status == null)
                ? courseRepository.findAll()
                : courseRepository.findAllByStatus(status);

        return courses.stream()
                .map(CourseResponse::from)
                .toList();
    }

    public CourseResponse getCourse(Long courseId) {
        return CourseResponse.from(findCourseById(courseId));
    }

    @Transactional
    public CourseResponse openCourse(Long courseId) {
        Course course = findCourseById(courseId);
        course.open();
        return CourseResponse.from(course);
    }

    @Transactional
    public CourseResponse closeCourse(Long courseId) {
        Course course = findCourseById(courseId);
        course.close();
        return CourseResponse.from(course);
    }

    public Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
    }

    public Course findCourseByIdWithLock(Long courseId) {
        return courseRepository.findWithLockById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
    }

    private void validateInstructor(User user) {
        if (user.getRole() != UserRole.INSTRUCTOR) {
            throw new BusinessException(ErrorCode.INVALID_USER_ROLE, "강사만 강의를 생성할 수 있습니다.");
        }
    }
}