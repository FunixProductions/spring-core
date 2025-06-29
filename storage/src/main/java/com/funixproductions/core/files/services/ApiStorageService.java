package com.funixproductions.core.files.services;

import com.funixproductions.core.crud.mappers.ApiMapper;
import com.funixproductions.core.crud.repositories.ApiRepository;
import com.funixproductions.core.crud.services.ApiService;
import com.funixproductions.core.exceptions.ApiBadRequestException;
import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.files.clients.StorageCrudClient;
import com.funixproductions.core.files.dtos.ApiStorageFileDTO;
import com.funixproductions.core.files.entities.ApiStorageFile;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Class used to handle files in spring applications.
 * Usage with FilesMultipart or just in services
 */
@Slf4j
public abstract class ApiStorageService<DTO extends ApiStorageFileDTO,
        ENTITY extends ApiStorageFile,
        MAPPER extends ApiMapper<ENTITY, DTO>,
        REPOSITORY extends ApiRepository<ENTITY>> extends ApiService<DTO, ENTITY, MAPPER, REPOSITORY> implements StorageCrudClient<DTO> {

    private static final String STORAGE_DIRECTORY = "storage-files";

    private final File storageDirectory;
    private final Cache<String, Resource> resourceCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

    /**
     * Constructor for storage service
     * @param serviceName the folder inside the storage directory, can't be null or empty
     * @param baseStorageDirectory the base storage directory, can't be null or empty
     */
    protected ApiStorageService(
            final @NonNull String serviceName,
            final @Nullable String baseStorageDirectory,
            final REPOSITORY repository,
            final MAPPER mapper
    ) {
        super(repository, mapper);

        if (Strings.isNullOrEmpty(serviceName)) {
            throw new ApiException("Service name cannot be null or empty");
        }else {
            this.storageDirectory = this.getStorageDirectory(baseStorageDirectory, serviceName);
        }
    }

    /**
     * Store a file in the storage directory
     * @param multipartFile the file to store, can't be null or empty
     */
    @Override
    @Transactional
    public DTO store(final DTO request, final MultipartFile multipartFile) {
        if (multipartFile == null || Strings.isNullOrEmpty(multipartFile.getResource().getFilename())) {
            throw new ApiBadRequestException("Le fichier ne peut pas être null ou vide");
        }

        final String originalFileName = multipartFile.getResource().getFilename();
        if (Strings.isNullOrEmpty(originalFileName)) {
            throw new ApiBadRequestException("Le nom du fichier ne peut pas être null ou vide");
        }

        request.setFileName(UUID.randomUUID() + "_" + originalFileName);
        request.setFileSize(multipartFile.getSize());
        request.setFileExtension(originalFileName.substring(originalFileName.lastIndexOf(".") + 1));
        request.setFilePath("toSet" + UUID.randomUUID());
        final DTO fileDto = super.create(request);

        return storeNewFile(multipartFile, fileDto);
    }

    @Override
    @Transactional
    public DTO updatePartial(DTO request, MultipartFile multipartFile) {
        request.setFileName(null);
        request.setFilePath(null);
        request.setFileSize(null);
        request.setFileExtension(null);
        final DTO dto = super.update(request);

        return updateFileDTO(multipartFile, dto);
    }

    @Override
    @Transactional
    public DTO updateFull(DTO request, MultipartFile multipartFile) {
        DTO dto = super.findById(request.getId().toString());
        request.setFileName(dto.getFileName());
        request.setFilePath(dto.getFilePath());
        request.setFileSize(dto.getFileSize());
        request.setFileExtension(dto.getFileExtension());
        dto = super.updatePut(request);

        return updateFileDTO(multipartFile, dto);
    }

    @Override
    @Transactional
    public Resource loadAsResource(final String fileId) {
        final DTO fileDto = super.findById(fileId);

        Resource resource = resourceCache.getIfPresent(fileId);
        if (resource != null) {
            return resource;
        }

        try {
            final Path filePath = Path.of(fileDto.getFilePath());

            resource = new ByteArrayResource(Files.readAllBytes(filePath));
            resourceCache.put(fileId, resource);
            return resource;
        } catch (Exception e) {
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
                    log.info("File {} has been deleted, path: {}", fileEnt.getId(), file.getPath());
                    this.resourceCache.invalidate(fileEnt.getId().toString());
                }
            } catch (final Exception e) {
                throw new ApiException(String.format("Le fichier %s n'a pas pu être supprimé. Erreur: %s.", fileEnt.getId(), e.getMessage()), e);
            }
        }
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void checkCache() {
        final List<String> uuids = new ArrayList<>(resourceCache.asMap().keySet());

        for (final ENTITY entity : super.getRepository().findAllByUuidIn(uuids)) {
            uuids.remove(entity.getUuid().toString());
        }

        for (final String uuid : uuids) {
            resourceCache.invalidate(uuid);
        }
    }

    public static byte[] createThumbnailFromImage(@NonNull MultipartFile originalFile, int width) {
        if (width <= 0) {
            throw new ApiBadRequestException("La redimention d'une image ne peut pas se faire avec une largeur de 0 ou moins.");
        }

        try (final ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream()) {
            BufferedImage img = ImageIO.read(originalFile.getInputStream());
            BufferedImage thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, Scalr.OP_ANTIALIAS);

            ImageIO.write(thumbImg, originalFile.getContentType().split("/")[1], thumbOutput);
            return thumbOutput.toByteArray();
        } catch (Exception e) {
            throw new ApiException("Erreur interne lors de la création d'une image en basse résolution.", e);
        }
    }

    @NonNull
    private DTO storeNewFile(MultipartFile multipartFile, DTO fileDto) {
        final String originalFileName = fileDto.getFileName();
        if (Strings.isNullOrEmpty(originalFileName)) {
            throw new ApiBadRequestException("Le nom du fichier ne peut pas être null ou vide");
        }

        final File file = new File(storageDirectory, originalFileName);

        fileDto.setFileName(originalFileName);
        fileDto.setFileSize(multipartFile.getSize());
        fileDto.setFileExtension(originalFileName.substring(originalFileName.lastIndexOf(".") + 1));
        fileDto.setFilePath(file.getAbsolutePath());
        fileDto = super.update(fileDto);

        try {
            if (!file.createNewFile()) {
                throw new ApiException(String.format("Le fichier %s n'a pas pu être créé", file.getPath()));
            }
            Files.write(file.toPath(), multipartFile.getBytes());
            log.info("File {} has been stored in {}", fileDto.getId(), file.getPath());
            return fileDto;
        } catch (final Exception e) {
            throw new ApiException(String.format("Le fichier n'a pas pu être enregistré. Erreur: %s.", e.getMessage()), e);
        }
    }

    private DTO updateFileDTO(MultipartFile multipartFile, DTO dto) {
        final File oldFile = new File(dto.getFilePath());

        try {
            Files.deleteIfExists(oldFile.toPath());
            log.info("File {} has been deleted, path: {}", dto.getId(), oldFile.getPath());

            dto.setFileName(UUID.randomUUID() + "_" + multipartFile.getResource().getFilename());
            this.resourceCache.invalidate(dto.getId().toString());
            return storeNewFile(multipartFile, dto);
        } catch (final Exception e) {
            throw new ApiException(String.format("Le fichier %s n'a pas pu être mis à jour. Erreur: %s.", dto.getId(), e.getMessage()), e);
        }
    }

    private File getStorageDirectory(final @Nullable String baseStorageDirectory, final @NonNull String serviceName) {
        final File baseDirectory = this.getBaseStorageDirectory(baseStorageDirectory);
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

    private File getBaseStorageDirectory(final @Nullable String baseStorageDirectory) {
        final File storageDirectory;

        if (Strings.isNullOrEmpty(baseStorageDirectory)) {
            storageDirectory = new File(STORAGE_DIRECTORY);
        } else {
            storageDirectory = new File(baseStorageDirectory, STORAGE_DIRECTORY);
        }

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
