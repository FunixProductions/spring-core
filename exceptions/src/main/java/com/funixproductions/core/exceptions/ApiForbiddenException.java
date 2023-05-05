package com.funixproductions.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ApiForbiddenException extends ApiException {
    public ApiForbiddenException(String message, Throwable e) {
        super(message, e);
    }

    public ApiForbiddenException(String message) {
        super(message);
    }
}
