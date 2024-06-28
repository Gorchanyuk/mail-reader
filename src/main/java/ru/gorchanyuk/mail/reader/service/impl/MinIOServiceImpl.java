package ru.gorchanyuk.mail.reader.service.impl;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gorchanyuk.mail.reader.model.AttachmentDto;
import ru.gorchanyuk.mail.reader.prop.MinIOProperties;
import ru.gorchanyuk.mail.reader.service.MinIOService;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinIOServiceImpl implements MinIOService {

    private final MinIOProperties properties;
    private final MinioClient client;

//    @Override
//    @EventListener(ApplicationReadyEvent.class)
//    public void createBucketIfNotExist(){
//        BucketExistsArgs bucketExist = BucketExistsArgs.builder()
//                .bucket(properties.getBucketName())
//                .build();
//        try {
//            if (!client.bucketExists(bucketExist)) {
//                client.makeBucket(
//                        MakeBucketArgs.builder()
//                                .bucket(properties.getBucketName())
//                                .build());
//                log.info("В хранилище MinIo создан Bucket с именем: {}", properties.getBucketName());
//            }
//        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
//                 IOException | InvalidResponseException | InternalException | InvalidKeyException |
//                 InsufficientDataException e) {
//            log.error("Произошла ошибка во время создания Bucket в ссервисе MinIo, проверьте работу сервиса. Сообщение об ошибке: {}",
//                    e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public AttachmentDto uploadFile(FileInputStream stream, String name, long sizeFile) {

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(properties.getBucketName())
                .object(name)
                .stream(stream, sizeFile, -1)
                .build();
        ObjectWriteResponse objectWriteResponse;
        try {
            objectWriteResponse = client.putObject(putObjectArgs);
        } catch (ErrorResponseException | NoSuchAlgorithmException | ServerException | XmlParserException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            log.error("Произошла ошибка при отправке файла: {} в сервис MinIO, проверьте работу сервиса. Сообщение об ошибке: {}"
                    , name,
                    e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Файл: {} успешно сохранен в сервисе MinIO", name);
        return AttachmentDto.builder()
                .bucket(objectWriteResponse.bucket())
                .fileName(objectWriteResponse.object())
                .build();
    }

//    public void getFile(String name) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//
//        File f = new File(name);
//        byte[] buf = new byte[4096];
//        try (InputStream stream =
//                     client.getObject(GetObjectArgs .builder()
//                             .bucket(properties.getBucketName())
//                             .object(name)
//                             .build());
//             FileOutputStream fos = new FileOutputStream(f)) {
//
//            int bytesRead;
//            while ((bytesRead = stream.read(buf)) != -1) {
//                fos.write(buf, 0, bytesRead);
//            }
//        }
//    }
}
