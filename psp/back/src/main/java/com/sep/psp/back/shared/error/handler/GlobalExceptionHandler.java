package com.sep.psp.back.shared.error.handler;

import com.sep.psp.back.shared.error.exception.AppException;
import com.sep.psp.back.shared.error.enumeration.ErrorCode;
import com.sep.psp.back.shared.error.response.ErrorResponse;
import com.sep.psp.back.shared.error.response.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        HttpStatus status = errorCode.getHttpStatus();

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                errorCode.getCode(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        HttpStatus status = errorCode.getHttpStatus();

        List<FieldErrorResponse> fieldErrors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorResponse)
                .toList();

        ErrorResponse response = ErrorResponse.validation(
                status.value(),
                status.getReasonPhrase(),
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        HttpStatus status = errorCode.getHttpStatus();

        List<FieldErrorResponse> fieldErrors = exception
                .getConstraintViolations()
                .stream()
                .map(violation -> new FieldErrorResponse(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        ErrorResponse response = ErrorResponse.validation(
                status.value(),
                status.getReasonPhrase(),
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        HttpStatus status = errorCode.getHttpStatus();

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                errorCode.getCode(),
                "Malformed request body.",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        HttpStatus status = errorCode.getHttpStatus();

        log.error(
                "Unexpected exception occurred on path: {}",
                request.getRequestURI(),
                exception
        );

        ErrorResponse response = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private FieldErrorResponse toFieldErrorResponse(FieldError fieldError) {
        return new FieldErrorResponse(
                fieldError.getField(),
                fieldError.getDefaultMessage()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(
            AuthenticationException exception,
            HttpServletRequest request
    ) {
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ErrorCode.UNAUTHORIZED.name(),
                "Invalid username or password.",
                request.getRequestURI(),
                List.of()
        );
    }

}
