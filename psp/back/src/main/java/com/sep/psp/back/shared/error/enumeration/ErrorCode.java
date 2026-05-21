package com.sep.psp.back.shared.error.enumeration;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    BAD_REQUEST(
            "BAD_REQUEST",
            "Bad request.",
            HttpStatus.BAD_REQUEST
    ),

    VALIDATION_ERROR(
            "VALIDATION_ERROR",
            "Request validation failed.",
            HttpStatus.BAD_REQUEST
    ),

    NOT_FOUND(
            "NOT_FOUND",
            "Requested resource was not found.",
            HttpStatus.NOT_FOUND
    ),

    UNAUTHORIZED(
            "UNAUTHORIZED",
            "Authentication is required.",
            HttpStatus.UNAUTHORIZED
    ),

    FORBIDDEN(
            "FORBIDDEN",
            "Access is forbidden.",
            HttpStatus.FORBIDDEN
    ),

    INTERNAL_SERVER_ERROR(
            "INTERNAL_SERVER_ERROR",
            "Unexpected server error.",
            HttpStatus.INTERNAL_SERVER_ERROR
    );

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCode(
            String code,
            String defaultMessage,
            HttpStatus httpStatus
    ) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
