package com.innogrid.uniq.apiopenstack.handler;

import com.innogrid.uniq.core.exception.UnAuthorizedException;
import com.innogrid.uniq.core.exception.CredentialException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;


@ControllerAdvice
public class ErrorHandler {

    private final Log logger = LogFactory.getLog(ErrorHandler.class);

    @ExceptionHandler({
            NumberFormatException.class,
            NullPointerException.class,
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class,
            MissingRequestHeaderException.class,
            SQLIntegrityConstraintViolationException.class,
            DuplicateKeyException.class,
            HttpMessageNotReadableException.class,
            CredentialException.class
    })
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handlerError400(Exception e) {
        return translate(e);
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleError500(HttpServletRequest request, Exception e) {
        return translate(e);
    }

//    @ExceptionHandler({AccessDeniedException.class})
//    @ResponseStatus(FORBIDDEN)
//    @ResponseBody
//    public Map<String, Object> handleError403(AccessDeniedException e) {
//        return translate(e);
//    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    @ResponseStatus(UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    public Map<String, Object> handleError415(Exception e) {
        return translate(e);
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseStatus(NOT_ACCEPTABLE)
    @ResponseBody
    public Map<String, Object> handleError406(Exception e) {
        return translate(e);
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleError404(Exception e) {
        return translate(e);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(METHOD_NOT_ALLOWED)
    @ResponseBody
    public Map<String, Object> handleError405(Exception e) {
        return translate(e);
    }

    @ExceptionHandler({AccessDeniedException.class, UnAuthorizedException.class})
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    public Map<String, Object> handleError401(Exception e) {
        return translate(e);
    }


    private Map<String, Object> translate(Throwable e) {

        String title;
        String detail;
        String type;

        Map<String, Object> errors = new HashMap<String, Object>();

        if(e instanceof AccessDeniedException || e instanceof UnAuthorizedException || e instanceof CredentialException) {
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
        } else if(e instanceof MissingServletRequestParameterException || e instanceof MissingRequestHeaderException) {
            title = e.getMessage();
            detail = e.getMessage();
            type = "https://uri.city-hub.kr/errors/BadRequestData";
        }  else if(e instanceof SQLIntegrityConstraintViolationException || e instanceof DuplicateKeyException) {
            title = e.getMessage();
            detail = e.getMessage();
            type = "4100";
        } else if(e instanceof NumberFormatException || e instanceof NullPointerException || e instanceof IllegalArgumentException || e instanceof HttpMessageNotReadableException) {
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
