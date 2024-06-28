package ru.gorchanyuk.mail.reader.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "new.folder.mail")
public class FolderProperties {

    private String readbox;
    private String inbox;
    private String sentbox;
}
