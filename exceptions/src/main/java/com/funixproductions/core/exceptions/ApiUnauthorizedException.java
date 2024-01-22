package com.funixproductions.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class ApiUnauthorizedException extends ApiException {
    public ApiUnauthorizedException(String message, Throwable e) {
        super(message, e);
    }

    public ApiUnauthorizedException(String message) {
        super(message);
    }
}
