package com.funixproductions.core.exceptions.handler;

import com.funixproductions.core.exceptions.ApiBadRequestException;
import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.exceptions.ApiForbiddenException;
import com.funixproductions.core.exceptions.ApiNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
        log.debug("Api Bad Request Exception ", e);
        return handleException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiExceptionResponse handleForbidden(ApiForbiddenException e) {
        log.debug("Api Forbidden Exception ", e);
        return handleException(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ApiNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiExceptionResponse handleNotFound(ApiNotFoundException e) {
        log.debug("Api Not Found Exception ", e);
        return handleException(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiExceptionResponse handleValidationException(MethodArgumentNotValidException ex) {
        final BindingResult bindingResult = ex.getBindingResult();
        final List<String> errorMessages = bindingResult.getFieldErrors()
                .stream()
                .map(this::buildErrorMessage)
                .toList();
        final String errorMessage = String.join(", ", errorMessages);

        return new ApiExceptionResponse(
                "Erreur requête : " + errorMessage,
                HttpStatus.BAD_REQUEST.value()
        );
    }

    private String buildErrorMessage(FieldError fieldError) {
        return fieldError.getField() + " " + fieldError.getDefaultMessage();
    }

    private ApiExceptionResponse handleException(Exception e, HttpStatus status) {
        return new ApiExceptionResponse(
                e.getMessage(),
                status.value()
        );
    }

}
