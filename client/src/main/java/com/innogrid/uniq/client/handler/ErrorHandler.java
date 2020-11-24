package com.innogrid.uniq.client.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.innogrid.uniq.client.exception.ValidationFailException;
import com.innogrid.uniq.core.exception.UnAuthorizedException;
import com.innogrid.uniq.core.exception.ErrorCode;
import com.innogrid.uniq.core.exception.ErrorResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;


@ControllerAdvice
public class ErrorHandler {

    private final Log logger = LogFactory.getLog(ErrorHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({
            ValidationFailException.class,
            NumberFormatException.class,
            NullPointerException.class,
            IllegalArgumentException.class,
            RequestRejectedException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseBody
    public Object handlerError400(Exception e) {
        logger.debug("Handle 400 error : '{}'", e);
//        final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST, e);
        return ResponseEntity.status(BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(translate(e));
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseBody
    public Object handleError403(AccessDeniedException e) {
        logger.debug("Handle 403 error : '{}'", e);
//        final ErrorResponse response = ErrorResponse.of(ErrorCode.FORBIDDEN, e);
        return ResponseEntity.status(UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body(translate(e));
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public Object handleError(Exception e) {
        logger.debug("Handle error : '{}'", e);
/*        ErrorCode errorCode = null;
        ErrorResponse response = null;

        try {
            errorCode = Enum.valueOf(ErrorCode.class, e.getClass().getSimpleName().toUpperCase()); // 에러코드에 메시지를 정의해놓았는지 확인
            response = ErrorResponse.of(errorCode, getMessage(errorCode, null));
        } catch (IllegalArgumentException ee) { // 에러코드에 정의하지 않은 에러는 기본 메시지로 반환
            errorCode = ErrorCode.BAD_REQUEST;
            response = ErrorResponse.of(errorCode, e);
        }*/

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(translate(e));
    }

    // RestTemplate으로 API 요청에서 익셉션(서비스가 사용 불가 or 서비스 동작 에러) 발생 시 메시지 처리 핸들러
    @ExceptionHandler({HttpStatusCodeException.class, HttpClientErrorException.class})
    @ResponseBody
    public Object handleErrorRestTemplate(Exception e) {
        logger.debug("Handle handleErrorRestTemplate : '{}'", e);

        ErrorCode errorCode = null;
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = null;
        String responseBody;
        String errorCodeStr;

        if (e instanceof HttpStatusCodeException) {
            // ResponseBody 값을 꺼내올 수 있도록 캐스팅
            HttpStatusCodeException httpStatusCodeException = ((HttpStatusCodeException) e);

            // timestamp, status, error, message, exception, target(target 정보는 커스텀 익셉션 발생 시 필요할 때만 담겨있음) 정보(api에서 핸들링해서 보내준 메시지)가 ResponseBody에 담겨 있음
            responseBody = httpStatusCodeException.getResponseBodyAsString();

            // 경우에 따라 에러 메시지를 처리할 수 있도록 메시지를 파싱
            jsonObj = parser.parse(responseBody).getAsJsonObject();

            // api 서버 사용 불가 시 에러 메시지 처리
            if (jsonObj.get("status").getAsString().equals("503")) {
                final ErrorResponse response = ErrorResponse.of(ErrorCode.SERVICE_UNAVAILABLE, jsonObj.get("timestamp").getAsString(), getMessage(ErrorCode.SERVICE_UNAVAILABLE, null));
                return new ResponseEntity<>(response, BAD_REQUEST);
            }

            // status 값이 BAD_REQUEST인 응답 중 vmware에서 발생한 에러 응답 처리
            if (jsonObj.get("error").getAsString().equals("BAD_REQUEST") && jsonObj.get("exception").getAsString().contains("RemoteException") || jsonObj.get("exception").getAsString().contains("vmware")) {
                final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST, jsonObj.get("timestamp").getAsString(), jsonObj.get("message").getAsString());
                return new ResponseEntity<>(response, BAD_REQUEST);
            }

            if (jsonObj.get("error").getAsString().equals("BAD_REQUEST") && jsonObj.get("exception").getAsString().contains("CloudException")) {
                final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST, jsonObj.get("timestamp").getAsString(), jsonObj.get("message").getAsString());
                return new ResponseEntity<>(response, BAD_REQUEST);
            }

            try {
                // api 서버 동작하지만, 에러 발생 했을 때 응답받은 에러 메시지
                errorCodeStr = jsonObj.get("error").getAsString();

                // 에러 코드에 정의 되어있는지 확인(존재한다면 해당 에러코드가 반환됨)
                errorCode = Enum.valueOf(ErrorCode.class, errorCodeStr);

            } catch (IllegalArgumentException ee) {
                // 에러 코드가 정의되어 있지 않은 경우 받은 메시지 그대로 반환함
                String timestamp = jsonObj.get("timestamp").getAsString();
                String status = jsonObj.get("status").getAsString();
                String error = jsonObj.get("error").getAsString();
                String msg = jsonObj.get("message").getAsString();

                final ErrorResponse response = ErrorResponse.of(timestamp, Integer.parseInt(status), error, msg);
                return new ResponseEntity<>(response, BAD_REQUEST);
            }
        } else if (e instanceof HttpClientErrorException) { // 위의 처리 로직과 동일함
            HttpClientErrorException httpStatusCodeException = ((HttpClientErrorException) e);
            responseBody = httpStatusCodeException.getResponseBodyAsString();
            jsonObj = parser.parse(responseBody).getAsJsonObject();

            if (jsonObj.get("status").getAsString().equals("503")) {
                final ErrorResponse response = ErrorResponse.of(ErrorCode.SERVICE_UNAVAILABLE, jsonObj.get("timestamp").getAsString(), getMessage(ErrorCode.SERVICE_UNAVAILABLE, null));
                return new ResponseEntity<>(response, SERVICE_UNAVAILABLE);
            }

            errorCodeStr = jsonObj.get("error").getAsString();

            try {
                errorCode = Enum.valueOf(ErrorCode.class, errorCodeStr);

            } catch (IllegalArgumentException ee) {
                String timestamp = jsonObj.get("timestamp").getAsString();
                String status = jsonObj.get("status").getAsString();
                String error = jsonObj.get("error").getAsString();
                String msg = jsonObj.get("message").getAsString();

                final ErrorResponse response = ErrorResponse.of(timestamp, Integer.parseInt(status), error, msg);
                return new ResponseEntity<>(response, BAD_REQUEST);
            }
        }

        JsonElement target = null;
        if (!(jsonObj.get("target") instanceof JsonNull)) {
            target = jsonObj.get("target");
        }

        final ErrorResponse response = ErrorResponse.of(errorCode, jsonObj.get("timestamp").getAsString(), getMessage(errorCode, target));
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    // 에러 코드 작성된 메시지 프로퍼티와 전달받은 타켓의 값에 따라 다국어 처리된 메시지를 반환하는 메소드
    public String getMessage(ErrorCode errorCode, JsonElement target) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = "";

        if (messageSource != null) {
            try {
                Object[] props = null;
                if (errorCode.getMessagePropertyPropsName() != null) {
                    int k = 0;
                    props = new Object[errorCode.getMessagePropertyPropsName().length];  // 메시지 프로퍼티에 들어가는 인자의 개수 만큼 props 초기화
                    for (int j = 0; j < errorCode.getMessagePropertyPropsName().length; j++) {
                        if (target == null && errorCode.getMessagePropertyPropsName().length != 0) {
                            MessageSourceResolvable value = new DefaultMessageSourceResolvable(errorCode.getMessagePropertyPropsName()[k]);
                            props[j] = value;
                            k++;
                        } else if (target.isJsonObject()) { // target의 값이 map이면 JsonObject로 전달되고 String 이면 JsonPrimary으로 전달됨
                            if (((JsonObject) target).has(errorCode.getMessagePropertyPropsName()[k])) { // target에 프로퍼티 이름이 존재한다면 props에 추가하며, 존재하지 않는다면 message.properies의 키값임
                                props[j] = ((JsonObject) target).get(errorCode.getMessagePropertyPropsName()[k]).toString().replaceAll("\"", "");
                                k++;
                            } else {
                                MessageSourceResolvable value = new DefaultMessageSourceResolvable(errorCode.getMessagePropertyPropsName()[k]);
                                props[j] = value;
                                k++;
                            }
                        } else {
                            if (errorCode.getMessagePropertyPropsName()[k].equals("$target")) { // target의 값을 String으로 넘겨줄 때 $target으로 전달
                                props[j] = target.toString().replaceAll("\"", "");
                            } else {
                                MessageSourceResolvable value = new DefaultMessageSourceResolvable(errorCode.getMessagePropertyPropsName()[k]);
                                props[j] = value;
                            }
                            k++;
                        }
                    }
                }
                message += messageSource.getMessage(errorCode.getMessageProperty(), props, locale);
            } catch (NoSuchMessageException ee) {
                logger.error(ee); // 메시지 프로퍼티값이 없을 때 발생하는 익셉션. 에러 message 값을 공백으로 반환
            }
        }
        return message;
    }

    private Map<String, Object> translate(Throwable e) {

        String title;
        String detail;
        String type;

        Map<String, Object> errors = new HashMap<String, Object>();

        if(e instanceof AccessDeniedException || e instanceof UnAuthorizedException) {
            if(e instanceof UnAuthorizedException) {
                UnAuthorizedException cue = (UnAuthorizedException) e;
                title = cue.getTitle();
                detail = cue.getDetail();
            } else {
                title = e.getMessage();
                detail = e.getMessage();
            }
            type = "https://uri.city-hub.kr/errors/Unauthorized";
        } else if(e instanceof HttpMediaTypeNotSupportedException) {
            HttpMediaTypeNotSupportedException hmtnse = (HttpMediaTypeNotSupportedException) e;
            title = hmtnse.getMessage();
            detail = hmtnse.getMessage();
            type = "https://uri.city-hub.kr/errors/MethodNotAllowed";
        } else if(e instanceof HttpMediaTypeNotAcceptableException) {
            HttpMediaTypeNotAcceptableException hmenae = (HttpMediaTypeNotAcceptableException) e;
            title = hmenae.getMessage();
            detail = hmenae.getMessage();
            type = "https://uri.city-hub.kr/errors/NotAcceptable";
        } else if(e instanceof NoHandlerFoundException) {
            NoHandlerFoundException nfe = (NoHandlerFoundException) e;
            title = nfe.getMessage();
            detail = nfe.getMessage();
            type = "https://uri.city-hub.kr/errors/ResourceNotFound";
        } else if(e instanceof HttpRequestMethodNotSupportedException) {
            HttpRequestMethodNotSupportedException mnae = (HttpRequestMethodNotSupportedException) e;
            title = mnae.getMessage();
            detail = mnae.getMessage();
            type = "https://uri.city-hub.kr/errors/MethodNotAllowed";
        } else if(e instanceof MissingServletRequestParameterException || e instanceof MissingRequestHeaderException || e instanceof ValidationFailException) {
            title = e.getMessage();
            detail = e.getMessage();
            type = "https://uri.city-hub.kr/errors/BadRequestData";
        }  else if(e instanceof SQLIntegrityConstraintViolationException || e instanceof DuplicateKeyException) {
            title = e.getMessage();
            detail = e.getMessage();
            type = "4100";
        } else if(e instanceof NumberFormatException || e instanceof NullPointerException || e instanceof IllegalArgumentException ||  e instanceof HttpMessageNotReadableException) {
            title = e.getMessage();
            detail = e.getMessage();
            type = "https://uri.city-hub.kr/errors/BadRequestData";
        } else {
            title = e.getMessage();
            detail = e.getMessage();
            type = "https://uri.city-hub.kr/errors/InternalError";
        }

        errors.put("type", type == null ? "" : type);
        errors.put("title", title == null ? "" : title);
        errors.put("detail", detail == null ? "" : detail);

        logger.error("Error - " + errors );

        e.printStackTrace();

        return errors;
    }
}