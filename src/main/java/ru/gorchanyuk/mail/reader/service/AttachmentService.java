package ru.gorchanyuk.mail.reader.service;

import jakarta.mail.Message;
import ru.gorchanyuk.mail.reader.model.AttachmentDto;

import java.util.List;

public interface AttachmentService {

    List<AttachmentDto> getAttachments(Message message, String folderName);
}
