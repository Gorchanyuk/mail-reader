package ru.gorchanyuk.mail.reader.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AppealDto implements Serializable {

    private String groups;
    private String theme;
    private String createdBy;
    private LocalDateTime created;
    private String text;
    private boolean isQuestion;
    private List<AttachmentDto> attachments;
}
