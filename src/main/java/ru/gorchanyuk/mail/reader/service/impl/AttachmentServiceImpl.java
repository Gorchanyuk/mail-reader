package ru.gorchanyuk.mail.reader.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.gorchanyuk.mail.reader.model.AttachmentDto;
import ru.gorchanyuk.mail.reader.service.AttachmentService;
import ru.gorchanyuk.mail.reader.service.DecoderService;
import ru.gorchanyuk.mail.reader.service.MinIOService;
import ru.gorchanyuk.mail.reader.service.ProxyMail;
import ru.gorchanyuk.mail.reader.util.ContentType;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class AttachmentServiceImpl implements AttachmentService {

    private final MinIOService minIOService;

    @Override
    public List<AttachmentDto> getAttachments(Message message, String folderName) {
        //Получает и сохраняет картинки из сообщения
        List<AttachmentDto> attachments = new ArrayList<>();
        if (hasAttachments(message)) {
            attachments = saveAttachments(message, folderName);
        }
        return attachments;
    }

    private boolean hasAttachments(Message message) {
        //Проверяет содержит ли сообщение файл
        if (ProxyMail.isEqualsType(message, ContentType.MIXED.getValue())) {
            Multipart mp = (Multipart) ProxyMail.getContent(message);
            return ProxyMail.getCount(mp) > 1;
        }
        return false;
    }

    private List<AttachmentDto> saveAttachments(Message message, String folderName) {

        List<AttachmentDto> attachments = new ArrayList<>();
        Multipart multipart = (Multipart) ProxyMail.getContent(message);
        for (int i = 0; i < ProxyMail.getCount(multipart); i++) {
            BodyPart bodyPart = ProxyMail.getBodyPart(multipart, i);

            if (!Part.ATTACHMENT.equalsIgnoreCase(ProxyMail.getDisposition(bodyPart)) &&
                    StringUtils.isBlank(ProxyMail.getFileName(bodyPart))) {
                //Если это не ATTACHMENT или имя файла не указано, пропускаем текущую часть и переходим к следующей
                continue;
            }
            String fileName = DecoderService.decode(ProxyMail.getFileName(bodyPart));
            AttachmentDto attachmentDto;
            try (InputStream is = ProxyMail.getInputStream(bodyPart)) {
                attachmentDto = uploadFile(is, fileName, folderName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            attachments.add(attachmentDto);
        }
        return attachments;
    }

    private AttachmentDto uploadFile(InputStream is, String fileName, String folder) {
        //Сохраняет файл и возвращает путь к нему
        AttachmentDto dto;
        fileName = LocalDate.now() + fileName;
        File tempFile = createTempFile(fileName);
        String pathForMinIO = "/" + folder + "/" + fileName;
        writeFile(is, tempFile);
        try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
            dto = minIOService.uploadFile(fileInputStream, pathForMinIO, tempFile.length());
        } catch (IOException e) {
            log.error("Произошла ошибка во время создания FileInputStream из файла: {}, для папки: {}",
                    fileName, folder);
            throw new RuntimeException(e);
        } finally {
            tempFile.delete();
        }

        return dto;
    }

    private void writeFile(InputStream is, File tempFile){
        byte[] buf = new byte[4096];
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            int bytesRead;
            while ((bytesRead = is.read(buf)) != -1) {
                fos.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error("Произошла ошибка во время записи данных в файл: {}. Причина ошибки: {}", tempFile, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static File createTempFile(String fileName) {
        File tempFile;
        try {
            tempFile = File.createTempFile(fileName, null);
        } catch (IOException e) {
            log.error("Не удалось создать временный файл с именем: {}", fileName);
            throw new RuntimeException(e);
        }
        return tempFile;
    }
}
