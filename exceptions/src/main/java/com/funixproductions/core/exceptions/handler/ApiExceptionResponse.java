package com.funixproductions.core.exceptions.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiExceptionResponse {
    private final String error;
    private final Integer status;
}
