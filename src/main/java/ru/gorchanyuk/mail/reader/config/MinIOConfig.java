package ru.gorchanyuk.mail.reader.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import ru.gorchanyuk.mail.reader.prop.MinIOProperties;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Конфигурация клиента MinIO
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinIOConfig {

    private final MinIOProperties properties;

    @Bean
    public MinioClient getClient() {
        return MinioClient.builder()
                .endpoint(properties.getUrl())
                .credentials(properties.getUsername(), properties.getPassword())
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createBucketIfNotExist(){
        MinioClient client = getClient();
        BucketExistsArgs bucketExist = BucketExistsArgs.builder()
                .bucket(properties.getBucketName())
                .build();
        try {
            if (!client.bucketExists(bucketExist)) {
                client.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(properties.getBucketName())
                                .build());
                log.info("В хранилище MinIo создан Bucket с именем: {}", properties.getBucketName());
            }
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InternalException | InvalidKeyException |
                 InsufficientDataException e) {
            log.error("Произошла ошибка во время создания Bucket в ссервисе MinIo, проверьте работу сервиса. Сообщение об ошибке: {}",
                    e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
