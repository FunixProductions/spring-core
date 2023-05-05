package com.funixproductions.core.tools.encryption;

import jakarta.persistence.AttributeConverter;

public interface ApiConverter<T> extends AttributeConverter<T, String> {
}
