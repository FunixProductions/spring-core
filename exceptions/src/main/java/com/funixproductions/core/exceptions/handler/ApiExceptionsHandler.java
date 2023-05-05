package com.funixproductions.core.exceptions.handler;

import com.funixproductions.core.exceptions.ApiBadRequestException;
import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.exceptions.ApiForbiddenException;
import com.funixproductions.core.exceptions.ApiNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionsHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiExceptionResponse handleBase(ApiException e) {
        return new ApiExceptionResponse(
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }

    @ExceptionHandler(ApiBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiExceptionResponse handleBadRequest(ApiBadRequestException e) {
        return new ApiExceptionResponse(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(ApiForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiExceptionResponse handleForbidden(ApiForbiddenException e) {
        return new ApiExceptionResponse(
                e.getMessage(),
                HttpStatus.FORBIDDEN.value()
        );
    }

    @ExceptionHandler(ApiNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiExceptionResponse handleNotFound(ApiNotFoundException e) {
        return new ApiExceptionResponse(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
    }

}
