package ru.gorchanyuk.mail.reader.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "props.minio")
public class MinIOProperties {

    private String bucketName;
    private String username;
    private String password;
    private String url;
}
