package org.example.enrollmentmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.enrollmentmanager.common.response.ApiResponse;
import org.example.enrollmentmanager.domain.course.CourseStatus;
import org.example.enrollmentmanager.dto.course.CourseResponse;
import org.example.enrollmentmanager.dto.course.CreateCourseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.enrollmentmanager.service.CourseService;

import java.util.List;

@Tag(name = "Course", description = "강의 관리 API")
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "강의 생성", description = "관리자가 새로운 강의를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request
    ) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "강의 생성이 완료되었습니다."));
    }

    @Operation(summary = "강의 목록 조회", description = "강의 상태값을 기준으로 강의 목록을 조회합니다. status가 없으면 전체 강의를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCourses(
            @RequestParam(required = false) CourseStatus status
    ) {
        List<CourseResponse> response = courseService.getCourses(status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "강의 단건 조회", description = "courseId를 기준으로 특정 강의 정보를 조회합니다.")
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(
            @PathVariable Long courseId
    ) {
        CourseResponse response = courseService.getCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "강의 오픈", description = "강의 상태를 OPEN으로 변경하여 수강 신청이 가능하도록 합니다.")
    @PatchMapping("/{courseId}/open")
    public ResponseEntity<ApiResponse<CourseResponse>> openCourse(
            @PathVariable Long courseId
    ) {
        CourseResponse response = courseService.openCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(response, "강의 상태가 OPEN으로 변경되었습니다."));
    }

    @Operation(summary = "강의 마감", description = "강의 상태를 CLOSED로 변경하여 수강 신청을 마감합니다.")
    @PatchMapping("/{courseId}/close")
    public ResponseEntity<ApiResponse<CourseResponse>> closeCourse(
            @PathVariable Long courseId
    ) {
        CourseResponse response = courseService.closeCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(response, "강의 상태가 CLOSED로 변경되었습니다."));
    }
}