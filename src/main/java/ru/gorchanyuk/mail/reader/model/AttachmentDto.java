package ru.gorchanyuk.mail.reader.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AttachmentDto implements Serializable {

    private String bucket;
    private String fileName;
}
