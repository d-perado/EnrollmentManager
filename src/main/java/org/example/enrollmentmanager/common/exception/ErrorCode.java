package org.example.enrollmentmanager.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),

    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "허용되지 않은 사용자 역할입니다."),
    INVALID_COURSE_STATUS(HttpStatus.BAD_REQUEST, "잘못된 강의 상태입니다."),
    COURSE_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "강의 정원이 초과되었습니다."),

    ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 수강 신청입니다."),
    DUPLICATE_ENROLLMENT(HttpStatus.CONFLICT, "이미 신청한 강의입니다."),
    COURSE_NOT_OPEN(HttpStatus.BAD_REQUEST, "모집 중인 강의가 아닙니다."),
    ENROLLMENT_PENDING_EXPIRED(HttpStatus.BAD_REQUEST, "결제 가능 시간이 만료되었습니다."),
    ENROLLMENT_NOT_CANCELABLE(HttpStatus.BAD_REQUEST, "취소할 수 없는 수강 신청입니다."),
    INVALID_ENROLLMENT_STATUS(HttpStatus.BAD_REQUEST, "잘못된 수강 신청 상태입니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}