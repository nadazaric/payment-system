package com.sep.psp.back.shared.error.exception;

import com.sep.psp.back.shared.error.enumeration.ErrorCode;

public class BadRequestException extends AppException {

    public BadRequestException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }

}
