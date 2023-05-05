package com.funixproductions.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ApiNotFoundException extends ApiException {
    public ApiNotFoundException(String message, Throwable e) {
        super(message, e);
    }

    public ApiNotFoundException(String message) {
        super(message);
    }
}
