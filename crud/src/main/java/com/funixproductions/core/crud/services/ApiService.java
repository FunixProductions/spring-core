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
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.*;

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
        final Specification<ENTITY> specificationSearch = getSpecification(search);
        final Pageable pageable = getPage(page, elemsPerPage, sort);
        final Page<DTO> toReturn = repository.findAll(specificationSearch, pageable).map(mapper::toDto);

        beforeSendingDTO(toReturn.getContent());
        return new PageDTO<>(toReturn);
    }

    @Override
    @NonNull
    @Transactional
    public DTO findById(String id) throws ApiNotFoundException {
        final Optional<ENTITY> search = repository.findByUuid(id);

        if (search.isPresent()) {
            final ENTITY entity = search.get();
            final DTO response = mapper.toDto(entity);

            beforeSendingDTO(Collections.singletonList(response));
            return response;
        } else {
            throw new ApiNotFoundException(String.format(MESSAGE_ENTITY_NOT_FOUND, id));
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

            if (actualDto != null)  {
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
    }

    @Override
    @Transactional
    public void delete(String id) {
        final Optional<ENTITY> search = repository.findByUuid(id);

        if (search.isPresent()) {
            final ENTITY entity = search.get();

            beforeDeletingEntity(Collections.singletonList(entity));
            repository.delete(entity);
        } else {
            throw new ApiNotFoundException(String.format(MESSAGE_ENTITY_NOT_FOUND, id));
        }
    }

    @Override
    @Transactional
    public void delete(String... ids) {
        final Set<String> idList = new HashSet<>(Arrays.asList(ids));
        final Iterable<ENTITY> search = this.repository.findAllByUuidIn(idList);

        beforeDeletingEntity(search);
        repository.deleteAll(search);
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
                final String[] sortElem = s.split(":");

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
            final String[] searchSplit = searchQuery.split(":");

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
