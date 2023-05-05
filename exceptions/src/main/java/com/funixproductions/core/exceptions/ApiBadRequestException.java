package com.funixproductions.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>Error class when a server error occurs. Error 500</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ApiBadRequestException extends ApiException {

    /**
     * @param message message error
     * @param e throw
     */
    public ApiBadRequestException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * @param message message error
     */
    public ApiBadRequestException(String message) {
        super(message);
    }
}
