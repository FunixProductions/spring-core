package com.funixproductions.core.crud.services;

import com.funixproductions.core.crud.clients.CrudClient;
import com.funixproductions.core.crud.dtos.ApiDTO;
import com.funixproductions.core.crud.dtos.PageDTO;
import com.funixproductions.core.crud.entities.ApiEntity;
import com.funixproductions.core.crud.mappers.ApiMapper;
import com.funixproductions.core.crud.repositories.ApiRepository;
import com.funixproductions.core.crud.services.search.SearchBuilder;
import com.funixproductions.core.exceptions.ApiBadRequestException;
import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.exceptions.ApiNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Getter
@RequiredArgsConstructor
public abstract class ApiService<DTO extends ApiDTO,
        ENTITY extends ApiEntity,
        MAPPER extends ApiMapper<ENTITY, DTO>,
        REPOSITORY extends ApiRepository<ENTITY>> implements CrudClient<DTO> {
    private static final String MESSAGE_ENTITY_NOT_FOUND = "L'entité id %s n'existe pas.";

    private final REPOSITORY repository;
    private final MAPPER mapper;

    @Override
    @Transactional
    public PageDTO<DTO> getAll(@Nullable String page, @Nullable String elemsPerPage, @Nullable String search, @Nullable String sort) {
        try {
            final Specification<ENTITY> specificationSearch = getSpecification(search);
            final Pageable pageable = getPage(page, elemsPerPage, sort);
            final Page<DTO> toReturn = repository.findAll(specificationSearch, pageable).map(mapper::toDto);

            beforeSendingDTO(toReturn.getContent());
            return new PageDTO<>(toReturn);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            final String errMessage = "Une erreur interne est survenue lors de la récupération des données.";

            log.error(errMessage, e);
            throw new ApiException(errMessage, e);
        }
    }

    @Override
    @NonNull
    @Transactional
    public DTO findById(String id) throws ApiNotFoundException {
        try {
            final Optional<ENTITY> search = repository.findByUuid(id);

            if (search.isPresent()) {
                final ENTITY entity = search.get();
                final DTO response = mapper.toDto(entity);

                beforeSendingDTO(Collections.singletonList(response));
                return response;
            } else {
                throw new ApiNotFoundException(String.format(MESSAGE_ENTITY_NOT_FOUND, id));
            }
        }  catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            final String errMessage = "Une erreur interne est survenue lors de la récupération de la donnée.";

            log.error(errMessage, e);
            throw new ApiException(errMessage, e);
        }
    }

    @NonNull
    @Override
    @Transactional
    public DTO create(DTO request) {
        final List<DTO> created = this.create(Collections.singletonList(request));

        if (created.size() == 1) {
            return created.get(0);
        } else {
            throw new ApiException("L'entité n'a pas été crée.");
        }
    }

    @Override
    @Transactional
    public List<DTO> create(List<@Valid DTO> request) {
        try {
            this.beforeMappingToEntity(request);
            final List<ENTITY> entities = new ArrayList<>();

            request.forEach(dto -> {
                final ENTITY actualEnt = mapper.toEntity(dto);

                actualEnt.setId(null);
                actualEnt.setUuid(null);
                this.afterMapperCall(dto, actualEnt);
                entities.add(actualEnt);
            });

            this.beforeSavingEntity(entities);
            final List<ENTITY> entitiesSaved = repository.saveAll(entities);
            this.afterSavingEntity(entitiesSaved);

            final List<DTO> toSend = new ArrayList<>();
            entitiesSaved.forEach(entity -> toSend.add(mapper.toDto(entity)));
            this.beforeSendingDTO(toSend);
            return toSend;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            final String errMessage = "Une erreur interne est survenue lors de la création d'entité.";

            log.error(errMessage, e);
            throw new ApiException(errMessage, e);
        }
    }

    @NonNull
    @Override
    @Transactional
    public DTO update(DTO request) throws ApiNotFoundException, ApiBadRequestException {
        if (request.getId() == null) {
            throw new ApiBadRequestException("Vous n'avez pas spécifié d'id.");
        }

        final List<DTO> response = update(Collections.singletonList(request));
        if (response.size() == 1) {
            return response.get(0);
        } else {
            throw new ApiNotFoundException(String.format(MESSAGE_ENTITY_NOT_FOUND, request.getId()));
        }
    }

    @Override
    @Transactional
    public List<DTO> update(List<DTO> request) {
        try {
            this.beforeMappingToEntity(request);

            final Set<String> ids = new HashSet<>();
            for (final DTO dto : request) {
                if (dto.getId() != null) {
                    ids.add(dto.getId().toString());
                }
            }
            final Iterable<ENTITY> entities = repository.findAllByUuidIn(ids);

            DTO actualDto;
            ENTITY entRequest;
            for (final ENTITY entity : entities) {
                actualDto = this.getDTOFromIdInList(request, entity.getUuid());

                if (actualDto != null) {
                    checkPatchEmptyVariables(actualDto);
                    entRequest = mapper.toEntity(actualDto);
                    this.afterMapperCall(actualDto, entRequest);
                    entRequest.setId(null);
                    entRequest.setUpdatedAt(Date.from(Instant.now()));
                    mapper.patch(entRequest, entity);
                    this.afterMapperCall(actualDto, entity);
                }
            }

            this.beforeSavingEntity(entities);
            final Iterable<ENTITY> entitiesSaved = repository.saveAll(entities);
            this.afterSavingEntity(entitiesSaved);

            final List<DTO> toSend = new ArrayList<>();
            entitiesSaved.forEach(entity -> toSend.add(mapper.toDto(entity)));
            beforeSendingDTO(toSend);
            return toSend;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            final String errMessage = "Une erreur interne est survenue lors de la mise à jour d'entité.";

            log.error(errMessage, e);
            throw new ApiException(errMessage, e);
        }
    }

    @Override
    @Transactional
    public DTO updatePut(DTO request) {
        if (request.getId() == null) {
            throw new ApiBadRequestException("Vous n'avez pas spécifié d'id.");
        }

        final List<DTO> response = updatePut(Collections.singletonList(request));
        if (response.size() == 1) {
            return response.get(0);
        } else {
            throw new ApiNotFoundException(String.format(MESSAGE_ENTITY_NOT_FOUND, request.getId()));
        }
    }

    @Override
    @Transactional
    public List<DTO> updatePut(List<@Valid DTO> request) {
        try {
            this.beforeMappingToEntity(request);
            final Set<String> ids = new HashSet<>();
            for (final DTO dto : request) {
                if (dto.getId() != null) {
                    ids.add(dto.getId().toString());
                }
            }
            final Iterable<ENTITY> entities = repository.findAllByUuidIn(ids);
            final Set<ENTITY> toSave = new HashSet<>();

            ENTITY entRequest;
            ENTITY toAdd;
            for (final DTO actualDto : request) {
                entRequest = this.getEntityFromUidInList(entities, actualDto.getId());

                if (entRequest != null) {
                    toAdd = mapper.toEntity(actualDto);
                    toAdd.setId(entRequest.getId());
                    toAdd.setCreatedAt(entRequest.getCreatedAt());
                    toAdd.setUpdatedAt(Date.from(Instant.now()));
                    this.afterMapperCall(actualDto, toAdd);
                    toSave.add(toAdd);
                }
            }

            this.beforeSavingEntity(toSave);
            final Iterable<ENTITY> entitiesSaved = repository.saveAll(toSave);
            this.afterSavingEntity(entitiesSaved);

            final List<DTO> toSend = new ArrayList<>();
            entitiesSaved.forEach(entity -> toSend.add(mapper.toDto(entity)));
            beforeSendingDTO(toSend);
            return toSend;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            final String errMessage = "Une erreur interne est survenue lors de la mise à jour complète d'entité.";

            log.error(errMessage, e);
            throw new ApiException(errMessage, e);
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        try {
            final Optional<ENTITY> search = repository.findByUuid(id);

            if (search.isPresent()) {
                final ENTITY entity = search.get();

                beforeDeletingEntity(Collections.singletonList(entity));
                repository.delete(entity);
            } else {
                throw new ApiNotFoundException(String.format(MESSAGE_ENTITY_NOT_FOUND, id));
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            final String errMessage = "Une erreur interne est survenue lors de la suppression d'entité.";

            log.error(errMessage, e);
            throw new ApiException(errMessage, e);
        }
    }

    @Override
    @Transactional
    public void delete(String... ids) {
        try {
            final Set<String> idList = new HashSet<>(Arrays.asList(ids));
            final Iterable<ENTITY> search = this.repository.findAllByUuidIn(idList);

            beforeDeletingEntity(search);
            repository.deleteAll(search);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            final String errMessage = "Une erreur interne est survenue lors de la suppression de plusieurs entités.";

            log.error(errMessage, e);
            throw new ApiException(errMessage, e);
        }
    }

    /**
     * Method used when you need to add some code logic before transform DTOs into ENTITIES
     * @param request dto list
     */
    public void beforeMappingToEntity(@NonNull Iterable<DTO> request) {
    }

    public void afterMapperCall(@NonNull DTO dto, @NonNull ENTITY entity) {
    }

    /**
     * Method used when you need to add some logic before saving an entity.
     * Override it when you have specific logic to add.
     *
     * @param entity entity list before save database
     */
    public void beforeSavingEntity(@NonNull Iterable<ENTITY> entity) {
    }

    /**
     * Method used when you need to add some logic after an entity save.
     * @param entity entity fetched from database.
     */
    public void afterSavingEntity(@NonNull Iterable<ENTITY> entity) {
    }

    /**
     * Method used to when you need to
     * add some logic before sending DTO to client.
     *
     * @param dto dto fetched from database.
     */
    public void beforeSendingDTO(@NonNull Iterable<DTO> dto) {
    }

    /**
     * Method used when you need to add some logic before removing an entity.
     * @param entity entity fetched from database.
     */
    public void beforeDeletingEntity(@NonNull Iterable<ENTITY> entity) {
    }

    private void checkPatchEmptyVariables(final DTO dto) {
        final Class<? extends ApiDTO> dtoClass = dto.getClass();
        final Field[] fields = dtoClass.getDeclaredFields();

        String value;
        for (final Field field : fields) {
            if (field.getType().equals(String.class)) {
                try {
                    field.setAccessible(true);
                    value = (String) field.get(dto);

                    if (Strings.isBlank(value)) {
                        field.set(dto, null);
                    }
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new ApiException("Une erreur interne est survenue lors de la mise à jour de l'entité.", e);
                }
            }
        }
    }

    private Pageable getPage(final String page, final String elemsPerPage, @Nullable final String sortQuery) {
        final Sort sort = getSort(sortQuery);
        final int nbrPage;
        final int maxPerPage;

        try {
            if (Strings.isEmpty(page)) {
                nbrPage = 0;
            } else {
                nbrPage = Integer.parseInt(page);
            }

            if (Strings.isEmpty(elemsPerPage)) {
                maxPerPage = 300;
            } else {
                int max = Integer.parseInt(elemsPerPage);
                if (max > 300) {
                    max = 300;
                }
                maxPerPage = max;
            }
        } catch (NumberFormatException e) {
            throw new ApiBadRequestException("Vous avez entré un nombre invalide.", e);
        }

        return PageRequest.of(nbrPage, maxPerPage, sort);
    }

    private Sort getSort(@Nullable final String sortQuery) throws ApiBadRequestException {
        if (Strings.isEmpty(sortQuery)) {
            return Sort.unsorted();
        } else {
            final String[] sort = sortQuery.split(",");
            final List<Sort.Order> orders = new ArrayList<>();

            for (final String s : sort) {
                final String[] sortElem = s.split(":", 2);

                if (sortElem.length == 2) {
                    final String field = sortElem[0];
                    final String order = sortElem[1];

                    if (order.equalsIgnoreCase("asc")) {
                        orders.add(Sort.Order.asc(field));
                    } else if (order.equalsIgnoreCase("desc")) {
                        orders.add(Sort.Order.desc(field));
                    } else {
                        throw new ApiBadRequestException("Vous avez entré un ordre de tri invalide. (asc ou desc)");
                    }
                } else {
                    throw new ApiBadRequestException("Vous avez entré un ordre de tri invalide. (asc ou desc)");
                }
            }

            return Sort.by(orders);
        }
    }

    @Nullable
    private Specification<ENTITY> getSpecification(@Nullable final String search) throws ApiBadRequestException {
        if (Strings.isEmpty(search)) {
            return null;
        }

        final SearchBuilder<ENTITY> searchBuilder = new SearchBuilder<>();
        final String[] searchs = search.split(",");

        for (final String searchQuery : searchs) {
            final String[] searchSplit = searchQuery.split(":", 3);

            if (searchSplit.length != 3) {
                throw new ApiBadRequestException("La recherche est invalide. Vous devez respecter le format suivant : key:operation:value");
            } else {
                final String key = searchSplit[0];
                final String operation = searchSplit[1];
                final String value = searchSplit[2];

                searchBuilder.with(key, operation, value);
            }
        }

        return searchBuilder.getSpecificationSearch();
    }

    /**
     * Finds a DTO from a given list and returns it
     * @param list dto list
     * @param idToFind uuid to find
     * @return dto found, null if not found
     */
    @Nullable
    public DTO getDTOFromIdInList(final Iterable<DTO> list, final UUID idToFind) {
        if (list == null || idToFind == null) {
            return null;
        }

        for (final DTO dto : list) {
            if (dto.getId() != null && dto.getId().equals(idToFind)) {
                return dto;
            }
        }
        return null;
    }

    /**
     * Finds an entity from a given list and returns it
     * @param list entity list
     * @param idToFind uuid to search
     * @return entity or null if not found
     */
    @Nullable
    public ENTITY getEntityFromUidInList(final Iterable<ENTITY> list, final UUID idToFind) {
        if (list == null || idToFind == null) {
            return null;
        }

        for (final ENTITY entity : list) {
            if (entity.getUuid() != null && entity.getUuid().equals(idToFind)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Finds an entity from a given list and returns it
     * @param list entity list
     * @param idToFind id long to search
     * @return entity or null if not found
     */
    @Nullable
    public ENTITY getEntityFromIdInList(final Iterable<ENTITY> list, final Long idToFind) {
        if (list == null || idToFind == null) {
            return null;
        }

        for (final ENTITY entity : list) {
            if (entity.getId() != null && entity.getId().equals(idToFind)) {
                return entity;
            }
        }
        return null;
    }

}
