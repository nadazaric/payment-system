package com.sep.bank.back.shared.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard API error response.")
public record ApiErrorResponse(

        @Schema(
                description = "Error timestamp.",
                example = "2026-06-12T12:30:00"
        )
        LocalDateTime timestamp,

        @Schema(
                description = "HTTP status code.",
                example = "400"
        )
        int status,

        @Schema(
                description = "Error name.",
                example = "Bad Request"
        )
        String error,

        @Schema(
                description = "Error message.",
                example = "Bank merchant does not exist."
        )
        String message,

        @Schema(
                description = "Request path.",
                example = "/api/bank/payments"
        )
        String path
) {
}