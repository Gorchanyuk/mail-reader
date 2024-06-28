package ru.gorchanyuk.mail.reader.service;

import ru.gorchanyuk.mail.reader.model.AttachmentDto;

import java.io.FileInputStream;

public interface MinIOService {

//    void createBucketIfNotExist();
    AttachmentDto uploadFile(FileInputStream stream, String name, long sizeFile);
}
