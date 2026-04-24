package org.example.enrollmentmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.enrollmentmanager.common.response.ApiResponse;
import org.example.enrollmentmanager.dto.enrollment.CreateEnrollmentRequest;
import org.example.enrollmentmanager.dto.enrollment.EnrollmentResponse;
import org.example.enrollmentmanager.service.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "03. Enrollment", description = "수강 신청 관리 API")
@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "08. 수강 신청", description = "사용자가 특정 강의에 수강 신청합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> createEnrollment(
            @Valid @RequestBody CreateEnrollmentRequest request
    ) {
        EnrollmentResponse response = enrollmentService.createEnrollment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "수강 신청이 완료되었습니다."));
    }

    @Operation(summary = "10. 내 수강 신청 목록 조회", description = "userId 기준으로 사용자의 수강 신청 내역을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<EnrollmentResponse>>> getMyEnrollments(
            @RequestParam Long userId,
            Pageable pageable
    ) {
        Page<EnrollmentResponse> response = enrollmentService.getMyEnrollments(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "09. 수강 신청 결제 확정", description = "결제를 확정하여 수강 신청 상태를 최종 완료 처리합니다.")
    @PatchMapping("/{enrollmentId}/confirm")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> confirmEnrollment(
            @PathVariable Long enrollmentId
    ) {
        EnrollmentResponse response = enrollmentService.confirmEnrollment(enrollmentId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "결제가 확정되어 수강 신청이 완료되었습니다.")
        );
    }

    @Operation(summary = "11. 수강 신청 취소", description = "사용자의 수강 신청을 취소합니다.")
    @PatchMapping("/{enrollmentId}/cancel")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> cancelEnrollment(
            @PathVariable Long enrollmentId
    ) {
        EnrollmentResponse response = enrollmentService.cancelEnrollment(enrollmentId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "수강 신청이 취소되었습니다.")
        );
    }
}