package com.funixproductions.core.crud.services.search;

import com.funixproductions.core.crud.entities.ApiEntity;
import com.funixproductions.core.exceptions.ApiBadRequestException;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.UUID;

/**
 * Search api engine
 */
@Getter
@RequiredArgsConstructor
public class ApiSearch<ENTITY extends ApiEntity> implements Specification<ENTITY> {

    private final transient Search search;

    @Override
    public Predicate toPredicate(@NonNull final Root<ENTITY> root,
                                 @NonNull final CriteriaQuery<?> query,
                                 @NonNull final CriteriaBuilder criteriaBuilder) {

        try {
            final String[] searchKeyParts = search.getKey().split("\\.");
            final Object valueSearch = castToRequiredType(root, getFieldType(root, searchKeyParts), search.getValue());

            if (searchKeyParts.length == 0) {
                throw new ApiBadRequestException("Veuillez spécifier un champ de recherche.");
            } else if (searchKeyParts.length == 1) {
                switch (search.getOperation()) {
                    case EQUALS -> {
                        return criteriaBuilder.equal(root.get(searchKeyParts[0]), valueSearch);
                    }
                    case NOT_EQUALS -> {
                        return criteriaBuilder.notEqual(root.get(searchKeyParts[0]), valueSearch);
                    }
                    case GREATER_THAN -> {
                        if (valueSearch instanceof final Date date) {
                            return criteriaBuilder.greaterThan(root.get(searchKeyParts[0]), date);
                        }
                        return criteriaBuilder.greaterThan(root.get(searchKeyParts[0]), valueSearch.toString());
                    }
                    case GREATER_THAN_OR_EQUAL_TO -> {
                        if (valueSearch instanceof final Date date) {
                            return criteriaBuilder.greaterThanOrEqualTo(root.get(searchKeyParts[0]), date);
                        }
                        return criteriaBuilder.greaterThanOrEqualTo(root.get(searchKeyParts[0]), valueSearch.toString());
                    }
                    case LESS_THAN -> {
                        if (valueSearch instanceof final Date date) {
                            return criteriaBuilder.lessThan(root.get(searchKeyParts[0]), date);
                        }
                        return criteriaBuilder.lessThan(root.get(searchKeyParts[0]), valueSearch.toString());
                    }
                    case LESS_THAN_OR_EQUAL_TO -> {
                        if (valueSearch instanceof final Date date) {
                            return criteriaBuilder.lessThanOrEqualTo(root.get(searchKeyParts[0]), date);
                        }
                        return criteriaBuilder.lessThanOrEqualTo(root.get(searchKeyParts[0]), valueSearch.toString());
                    }
                    case LIKE -> {
                        return criteriaBuilder.like(root.get(searchKeyParts[0]), "%" + valueSearch + "%");
                    }
                    case NOT_LIKE -> {
                        return criteriaBuilder.notLike(root.get(searchKeyParts[0]), "%" + valueSearch + "%");
                    }
                    case IS_NULL -> {
                        return criteriaBuilder.isNull(root.get(searchKeyParts[0]));
                    }
                    case IS_NOT_NULL -> {
                        return criteriaBuilder.isNotNull(root.get(searchKeyParts[0]));
                    }
                    case IS_TRUE -> {
                        return criteriaBuilder.isTrue(root.get(searchKeyParts[0]));
                    }
                    case IS_FALSE -> {
                        return criteriaBuilder.isFalse(root.get(searchKeyParts[0]));
                    }
                    default -> throw new ApiBadRequestException("Operation " + search.getOperation() + " is not supported.");
                }
            } else {
                Join<ENTITY, ?> subObjectJoin = root.join(searchKeyParts[0], JoinType.LEFT);
                for (int i = 1; i < searchKeyParts.length - 1; i++) {
                    subObjectJoin = subObjectJoin.join(searchKeyParts[i], JoinType.LEFT);
                }

                switch (search.getOperation()) {
                    case EQUALS -> {
                        return criteriaBuilder.equal(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), valueSearch);
                    }
                    case NOT_EQUALS -> {
                        return criteriaBuilder.notEqual(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), valueSearch);
                    }
                    case GREATER_THAN -> {
                        if (valueSearch instanceof final Date date) {
                            return criteriaBuilder.greaterThan(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), date);
                        }
                        return criteriaBuilder.greaterThan(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), valueSearch.toString());
                    }
                    case GREATER_THAN_OR_EQUAL_TO -> {
                        if (valueSearch instanceof final Date date) {
                            return criteriaBuilder.greaterThanOrEqualTo(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), date);
                        }
                        return criteriaBuilder.greaterThanOrEqualTo(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), valueSearch.toString());
                    }
                    case LESS_THAN -> {
                        if (valueSearch instanceof final Date date) {
                            return criteriaBuilder.lessThan(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), date);
                        }
                        return criteriaBuilder.lessThan(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), valueSearch.toString());
                    }
                    case LESS_THAN_OR_EQUAL_TO -> {
                        if (valueSearch instanceof final Date date) {
                            return criteriaBuilder.lessThanOrEqualTo(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), date);
                        }
                        return criteriaBuilder.lessThanOrEqualTo(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), valueSearch.toString());
                    }
                    case LIKE -> {
                        return criteriaBuilder.like(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), "%" + valueSearch + "%");
                    }
                    case NOT_LIKE -> {
                        return criteriaBuilder.notLike(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]), "%" + valueSearch + "%");
                    }
                    case IS_NULL -> {
                        return criteriaBuilder.isNull(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]));
                    }
                    case IS_NOT_NULL -> {
                        return criteriaBuilder.isNotNull(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]));
                    }
                    case IS_TRUE -> {
                        return criteriaBuilder.isTrue(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]));
                    }
                    case IS_FALSE -> {
                        return criteriaBuilder.isFalse(subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]));
                    }
                    default -> throw new ApiBadRequestException("Operation " + search.getOperation() + " is not supported.");
                }
            }
        } catch (IllegalArgumentException e) {
            throw new ApiBadRequestException("Le champ de recherche " + search.getKey() + " n'existe pas.", e);
        }
    }

    private Class<?> getFieldType(Root<ENTITY> root, String[] searchKeyParts) {
        if (searchKeyParts.length == 1) {
            return root.get(searchKeyParts[0]).getJavaType();
        } else {
            Join<ENTITY, ?> subObjectJoin = root.join(searchKeyParts[0], JoinType.LEFT);

            for (int i = 1; i < searchKeyParts.length - 1; i++) {
                subObjectJoin = subObjectJoin.join(searchKeyParts[i], JoinType.LEFT);
            }
            return subObjectJoin.get(searchKeyParts[searchKeyParts.length - 1]).getJavaType();
        }
    }

    @NonNull
    private Object castToRequiredType(@NonNull final Root<ENTITY> root,
                                      @NonNull final Class<?> fieldType,
                                      @NonNull final String value) {
        try {
            if (fieldType.isAssignableFrom(Double.class)) {
                return Double.valueOf(value);
            } else if (fieldType.isAssignableFrom(Integer.class)) {
                return Integer.valueOf(value);
            } else if (fieldType.isAssignableFrom(Long.class)) {
                return Long.parseLong(value);
            } else if (fieldType.isAssignableFrom(String.class)) {
                return value;
            } else if (fieldType.isEnum()) {
                return Enum.valueOf((Class<? extends Enum>) fieldType, value);
            } else if (fieldType.isAssignableFrom(Boolean.class)) {
                return Boolean.valueOf(value);
            } else if (fieldType.isAssignableFrom(UUID.class)) {
                return UUID.fromString(value);
            } else if (fieldType.isAssignableFrom(Float.class)) {
                return Float.valueOf(value);
            } else if (fieldType.isAssignableFrom(Date.class)) {
                return Date.from(Instant.parse(value));
            } else if (fieldType.isAssignableFrom(Timestamp.class)) {
                return Timestamp.from(Instant.parse(value));
            } else if (fieldType.isAnnotationPresent(Entity.class)) {
                return retrieveTypeFromEntity(root, fieldType, value);
            } else {
                throw new ApiBadRequestException("Le type " + fieldType + " n'est pas supporté.");
            }
        } catch (DateTimeParseException e) {
            throw new ApiBadRequestException("Impossible de parser la valeur date " + value + " en " + fieldType.getName(), e);
        }
    }

    @NonNull
    private Object retrieveTypeFromEntity(@NonNull final Root<ENTITY> root,
                                          @NonNull final Class<?> fieldType,
                                          @NonNull final String value) {
        final String[] parts = search.getKey().split("\\.", 2);
        final String subObjectName = parts[0];
        final String subObjectFieldName = parts.length == 2 ? parts[1] : null;

        Field subObjectField;
        try {
            subObjectField = fieldType.getDeclaredField(subObjectName);
            subObjectField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ApiBadRequestException("Le sous objet " + subObjectName + " n'existe pas dans " + fieldType.getName(), e);
        }

        Object subObject;
        try {
            subObject = subObjectField.get(root.get(subObjectName));
        } catch (IllegalAccessException e) {
            throw new ApiBadRequestException("Impossible d'acceder au sous objet " + subObjectName + " dans " + fieldType.getName(), e);
        }

        if (subObject == null) {
            throw new ApiBadRequestException("Sous-Objet " + subObjectName + " est null dans " + fieldType.getName());
        }
        if (subObjectFieldName != null) {
            try {
                Field subObjectField2 = subObject.getClass().getDeclaredField(subObjectFieldName);
                subObjectField2.setAccessible(true);
                return castToRequiredType(root, subObjectField2.getType(), value);
            } catch (NoSuchFieldException e) {
                throw new ApiBadRequestException("Le champ " + subObjectFieldName + " n'existe pas dans le sous objet " + subObjectName, e);
            }
        } else {
            return subObject;
        }
    }
}
