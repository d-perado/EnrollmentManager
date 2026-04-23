package controller;

import common.response.ApiResponse;
import domain.course.CourseStatus;
import dto.course.CourseResponse;
import dto.course.CreateCourseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request
    ) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "강의 생성이 완료되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCourses(
            @RequestParam(required = false) CourseStatus status
    ) {
        List<CourseResponse> response = courseService.getCourses(status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(
            @PathVariable Long courseId
    ) {
        CourseResponse response = courseService.getCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{courseId}/open")
    public ResponseEntity<ApiResponse<CourseResponse>> openCourse(
            @PathVariable Long courseId
    ) {
        CourseResponse response = courseService.openCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(response, "강의 상태가 OPEN으로 변경되었습니다."));
    }

    @PatchMapping("/{courseId}/close")
    public ResponseEntity<ApiResponse<CourseResponse>> closeCourse(
            @PathVariable Long courseId
    ) {
        CourseResponse response = courseService.closeCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(response, "강의 상태가 CLOSED로 변경되었습니다."));
    }
}