package com.funixproductions.core.crud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchOperation {
    EQUALS("eq"),
    EQUALS_IGNORE_CASE("eqic"),
    NOT_EQUALS("neq"),
    GREATER_THAN("gt"),
    GREATER_THAN_OR_EQUAL_TO("gte"),
    LESS_THAN("lt"),
    LESS_THAN_OR_EQUAL_TO("lte"),
    LIKE("like"),
    LIKE_IGNORE_CASE("likeic"),
    STARTS_WITH("sw"),
    STARTS_WITH_IGNORE_CASE("swic"),
    ENDS_WITH("ew"),
    ENDS_WITH_IGNORE_CASE("ewic"),
    NOT_LIKE("nlike"),
    IS_TRUE("istrue"),
    IS_FALSE("isfalse"),
    IS_NULL("isnull"),
    IS_NOT_NULL("isnotnull");

    private final String operation;
}
