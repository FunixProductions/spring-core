package com.funixproductions.core.exceptions.handler;

import lombok.Getter;

@Getter
public class ApiExceptionResponse {
    private final String error;
    private final Integer status;
    private final long timestamp;

    public ApiExceptionResponse(final String error, final Integer status) {
        this.error = error;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }
}
