package com.funixproductions.core.files.services;

import com.funixproductions.core.crud.repositories.ApiRepository;
import com.funixproductions.core.crud.services.ApiService;
import com.funixproductions.core.exceptions.ApiBadRequestException;
import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.files.clients.StorageCrudClient;
import com.funixproductions.core.files.dtos.ApiStorageFileDTO;
import com.funixproductions.core.files.entities.ApiStorageFile;
import com.funixproductions.core.files.mappers.ApiStorageMapper;
import com.google.common.base.Strings;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.awt.geom.IllegalPathStateException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class used to handle files in spring applications.
 * Usage with FilesMultipart or just in services
 */
@Slf4j
public abstract class ApiStorageService<DTO extends ApiStorageFileDTO,
        ENTITY extends ApiStorageFile,
        MAPPER extends ApiStorageMapper<ENTITY, DTO>,
        REPOSITORY extends ApiRepository<ENTITY>> extends ApiService<DTO, ENTITY, MAPPER, REPOSITORY> implements StorageCrudClient<DTO> {

    private static final String STORAGE_DIRECTORY = "storage-files";

    private final File storageDirectory;

    /**
     * Constructor for storage service
     * @param serviceName the folder inside the storage directory, can't be null or empty
     */
    protected ApiStorageService(final String serviceName,
                      final REPOSITORY repository,
                      final MAPPER mapper) {
        super(repository, mapper);

        if (Strings.isNullOrEmpty(serviceName)) {
            throw new ApiException("Service name cannot be null or empty");
        } else {
            this.storageDirectory = getStorageDirectory(serviceName);
        }
    }

    /**
     * Store a file in the storage directory
     * @param multipartFile the file to store, can't be null or empty
     */
    @Transactional
    public DTO store(final MultipartFile multipartFile) {
        if (multipartFile == null || Strings.isNullOrEmpty(multipartFile.getResource().getFilename())) {
            throw new ApiBadRequestException("Le fichier ne peut pas être null ou vide");
        }

        DTO fileDto = generateDtoFromMultipartFile(multipartFile);
        final File file = new File(storageDirectory, fileDto.getId() + "-" + multipartFile.getResource().getFilename());
        fileDto.setFilePath(file.getPath());
        fileDto = super.update(fileDto);

        try {
            multipartFile.transferTo(file);
            log.info("File {} has been stored in {}", fileDto.getId(), file.getPath());
            return fileDto;
        } catch (final IOException | IllegalStateException e) {
            throw new ApiException(String.format("Le fichier n'a pas pu être enregistré. Erreur: %s.", e.getMessage()), e);
        }
    }

    public Resource loadAsResource(final String fileId) {
        final DTO fileDto = super.findById(fileId);

        try {
            final Path filePath = Path.of(fileDto.getFilePath());
            return new ByteArrayResource(Files.readAllBytes(filePath));
        } catch (IllegalPathStateException | IOException e) {
            throw new ApiException(String.format("Le fichier %s n'a pas pu être chargé. Erreur: %s.", fileId, e.getMessage()), e);
        }
    }

    @Override
    public void beforeDeletingEntity(@NonNull Iterable<ENTITY> filesEnt) {
        File file;

        for (final ENTITY fileEnt : filesEnt) {
            file = new File(fileEnt.getFilePath());

            try {
                if (file.exists()) {
                    Files.deleteIfExists(file.toPath());
                }
            } catch (final IOException e) {
                log.error("File {} has not been deleted", fileEnt.getId(), e);
            }
        }
    }

    private DTO generateDtoFromMultipartFile(final MultipartFile multipartFile) {
        final DTO fileDto = getMapper().toDto(multipartFile);
        final String originalFileName = multipartFile.getResource().getFilename();
        if (Strings.isNullOrEmpty(originalFileName)) {
            throw new ApiBadRequestException("Le nom du fichier ne peut pas être null ou vide");
        }

        fileDto.setFilePath("toSet");
        fileDto.setFileExtension(originalFileName.substring(originalFileName.lastIndexOf(".") + 1));
        return super.create(fileDto);
    }

    private static File getStorageDirectory(final String serviceName) {
        final File baseDirectory = getBaseStorageDirectory();
        final File storageDirectory = new File(baseDirectory, serviceName);

        if (!storageDirectory.exists()) {
            if (storageDirectory.mkdir()) {
                log.info("Storage service {} directory created", serviceName);
                return storageDirectory;
            } else {
                throw new ApiException(String.format("Storage service %s directory could not be created", serviceName));
            }
        } else {
            return storageDirectory;
        }
    }

    private static File getBaseStorageDirectory() {
        final File storageDirectory = new File(STORAGE_DIRECTORY);

        if (!storageDirectory.exists()) {
            if (storageDirectory.mkdir()) {
                log.info("Storage base directory created");
                return storageDirectory;
            } else {
                throw new ApiException("Storage base directory could not be created");
            }
        } else {
            return storageDirectory;
        }
    }

}
