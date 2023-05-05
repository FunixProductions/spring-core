package com.funixproductions.core.crud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchOperation {
    EQUALS("eq"),
    NOT_EQUALS("neq"),
    GREATER_THAN("gt"),
    GREATER_THAN_OR_EQUAL_TO("gte"),
    LESS_THAN("lt"),
    LESS_THAN_OR_EQUAL_TO("lte"),
    LIKE("like"),
    NOT_LIKE("nlike"),
    IS_TRUE("istrue"),
    IS_FALSE("isfalse"),
    IS_NULL("isnull"),
    IS_NOT_NULL("isnotnull");

    private final String operation;
}
