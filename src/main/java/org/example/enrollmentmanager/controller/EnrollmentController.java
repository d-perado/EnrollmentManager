package org.example.enrollmentmanager.controller;

import org.example.enrollmentmanager.common.response.ApiResponse;
import org.example.enrollmentmanager.dto.enrollment.CreateEnrollmentRequest;
import org.example.enrollmentmanager.dto.enrollment.EnrollmentResponse;
import org.example.enrollmentmanager.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> createEnrollment(
            @Valid @RequestBody CreateEnrollmentRequest request
    ) {
        EnrollmentResponse response = enrollmentService.createEnrollment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "수강 신청이 완료되었습니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(
            @RequestParam Long userId
    ) {
        List<EnrollmentResponse> response = enrollmentService.getMyEnrollments(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{enrollmentId}/confirm")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> confirmEnrollment(
            @PathVariable Long enrollmentId
    ) {
        EnrollmentResponse response = enrollmentService.confirmEnrollment(enrollmentId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "결제가 확정되어 수강 신청이 완료되었습니다.")
        );
    }
}