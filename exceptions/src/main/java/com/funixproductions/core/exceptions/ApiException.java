package com.funixproductions.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ApiException extends RuntimeException {
    public ApiException(String message, Throwable e) {
        super(message, e);
    }

    public ApiException(String message) {
        super(message);
    }
}
