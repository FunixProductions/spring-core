package com.funixproductions.core.crud.services.search;

import com.funixproductions.core.crud.enums.SearchOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Search {
    private final String key;
    private final SearchOperation operation;
    private final String value;
}
