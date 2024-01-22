package com.funixproductions.core.exceptions.handler;

import com.funixproductions.core.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ApiExceptionsHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiExceptionResponse handleBase(ApiException e) {
        log.error("Api Exception ", e);
        return handleException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApiBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiExceptionResponse handleBadRequest(ApiBadRequestException e) {
        return handleException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiExceptionResponse handleForbidden(ApiForbiddenException e) {
        return handleException(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ApiNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiExceptionResponse handleNotFound(ApiNotFoundException e) {
        return handleException(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApiUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiExceptionResponse handleUnauthorized(ApiUnauthorizedException e) {
        return handleException(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiExceptionResponse handleValidationException(MethodArgumentNotValidException ex) {
        final BindingResult bindingResult = ex.getBindingResult();
        final List<ApiExceptionResponse.FieldError> fieldErrors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> new ApiExceptionResponse.FieldError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        return new ApiExceptionResponse(
                "La requête est invalide. Veuillez vérifier les champs.",
                HttpStatus.BAD_REQUEST.value(),
                fieldErrors
        );
    }

    private ApiExceptionResponse handleException(Exception e, HttpStatus status) {
        return new ApiExceptionResponse(
                e.getMessage(),
                status.value()
        );
    }

}
