package com.innogrid.uniq.core.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Cilent 모듈 에러 메세지 포멧
 * 에러 코드가 정의되어 있지 않은 경우 발생한 에러에서 파싱하여 값을 전달
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String timestamp; // 에러 발생 시간
    private int status; // 에러의 상태 코드
    private String error; // 에러 코드명
    private String message; // 에러 메시지

    private ErrorResponse(ErrorCode code, Exception e) {
        this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
        this.status = code.getStatus();
        this.error = code.name();
        this.message = e.getMessage();
    }

    private ErrorResponse(final ErrorCode code, String message) {
        this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
        this.status = code.getStatus();
        this.error = code.name();
        this.message = message;
    }

    private ErrorResponse(final ErrorCode code, String time, String message) {
        this.timestamp = time;
        this.status = code.getStatus();
        this.error = code.name();
        this.message = message;
    }

    public ErrorResponse(String timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    // 에러코드에 정의 되어 있는 경우
    public static ErrorResponse of(final ErrorCode code, Exception e) {
        return new ErrorResponse(code, e);
    }

    public static ErrorResponse of(final ErrorCode code, String message) {
        return new ErrorResponse(code, message);
    }

    public static ErrorResponse of(final ErrorCode code, String time, String message) {
        return new ErrorResponse(code, time, message);
    }

    public static ErrorResponse of(String timestamp, int status, String error, String message) {
        return new ErrorResponse(timestamp, status, error, message);
    }
}