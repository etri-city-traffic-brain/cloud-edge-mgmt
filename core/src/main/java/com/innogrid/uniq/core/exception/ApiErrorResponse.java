package com.innogrid.uniq.core.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * API 모듈 에러 메세지 포멧
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiErrorResponse {

    private Timestamp timestamp; // 에러 발생 시간
    private int status; // 에러의 상태 코드
    private String error; // 에러 코드명
    private String message; // 에러 메시지
    private String exception; // 익셉션이 발생한 클래스명
    private Object target; // client단에 exception이 발생한 타겟 정보 전달

    private ApiErrorResponse(final ErrorCode code, Exception e) { // 기본 익셉션 및 커스텀 익센션 처리
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.status = code.getStatus();
        this.error = code.name();
        this.exception = e.getClass().getName();
        this.message = e.getMessage();
        if (e instanceof ApiException) { // 커스컴 익셉션은 타겟값을 가지고 있으므로 타겟 값을 꺼내기 위해 체크
            if (((ApiException) e).getTarget() != null) {
                target = ((ApiException) e).getTarget();
            }
        }
    }

    private ApiErrorResponse(ErrorCode code, Exception e, String message) { // Vmware에서 발생한 익셉션 메시지를 처리
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.status = code.getStatus();
        this.error = code.name();
        this.exception = e.getClass().getName();
        this.message = message;
    }

    public static ApiErrorResponse of(final ErrorCode code, Exception e) {
        return new ApiErrorResponse(code, e);
    }

    public static ApiErrorResponse of(final ErrorCode code, final Exception e, final String message) {
        return new ApiErrorResponse(code, e, message);
    }
}