package com.funixproductions.core.exceptions.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class ApiExceptionResponse {
    private final String error;
    private final Integer status;
    private final List<FieldError> fieldErrors;
    private final long timestamp;

    public ApiExceptionResponse(final String error, final Integer status) {
        this.error = error;
        this.status = status;
        this.fieldErrors = Collections.emptyList();
        this.timestamp = System.currentTimeMillis();
    }

    public ApiExceptionResponse(final String error, final Integer status, final List<FieldError> fieldErrors) {
        this.error = error;
        this.status = status;
        this.fieldErrors = fieldErrors;
        this.timestamp = System.currentTimeMillis();
    }

    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private final String field;
        private final String message;
    }
}
