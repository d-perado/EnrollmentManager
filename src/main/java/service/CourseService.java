package service;

import common.exception.BusinessException;
import common.exception.ErrorCode;
import domain.course.Course;
import domain.course.CourseStatus;
import domain.user.User;
import domain.user.UserRole;
import dto.course.CourseResponse;
import dto.course.CreateCourseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CourseRepository;

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

    private void validateInstructor(User user) {
        if (user.getRole() != UserRole.INSTRUCTOR) {
            throw new BusinessException(ErrorCode.INVALID_USER_ROLE, "강사만 강의를 생성할 수 있습니다.");
        }
    }
}