package com.sep.psp.back.shared.error.response;

public record FieldErrorResponse(
        String field,
        String message
) {
}
