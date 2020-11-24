package com.innogrid.uniq.core.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    /**
     * @param status 에러코드 맞는 적절한 상태코드
     * @param messageProperty 에러 메시지 다국어처리를 위한 메시지 프로퍼티
     * @param messagePropertyName 메시지 프로퍼티에 인자로 들어갈 target값(Map과 String 형태 가능, Map은 key값이 들어가고 String은 $target으로 표기)이나 메시지 프로퍼티 값
     */
    BAD_REQUEST(400, "s.exception-400", null),
    FORBIDDEN(403, "s.exception-403", null),
    INTERNAL_SERVER_ERROR(500, "s.exception-500", null),

    SERVICE_UNAVAILABLE(503, "s.service-unavailable", null), // api 서버 접속 불가 시

    MALFORMEDURLEXCEPTION(400, "s.exception-malformed-url", null),
    FILENOTFOUNDEXCEPTION(400, "s.exception-file-not-found", null),
    RESOURCEACCESSEXCEPTION(408, "s.exception-timeout", null),
    CLIENTABORTEXCEPTION(408, "s.timeout-exception", null),

    CREDENTIAL_INVALID(400, "s.t.not-exist", new String[]{"w.credential"}),
    VM_CREATE_FAIL(400, "s.t.fail", new String[]{"w.create"}),
    VM_DELETE_FAIL(400, "s.t.fail", new String[]{"w.delete"}),
    VM_ACTION_FAIL(400, "s.t.fail", new String[]{"w.action"});

    private int status;
    private final String messageProperty;
    private final String[] messagePropertyPropsName;

    ErrorCode(final int status, String messageProperty, String[] messagePropertyPropsName) {
        this.status = status;
        this.messageProperty = messageProperty;
        this.messagePropertyPropsName = messagePropertyPropsName;
    }

    public int getStatus() {
        return status;
    }

    public String getMessageProperty() {
        return messageProperty;
    }

    public String[] getMessagePropertyPropsName() {
        return messagePropertyPropsName;
    }
}