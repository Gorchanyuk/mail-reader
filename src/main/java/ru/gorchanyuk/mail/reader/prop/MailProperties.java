package ru.gorchanyuk.mail.reader.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail.imaps")
public class MailProperties {

    private String transportProtocol;
    private String host;
    private Integer port;
    private String user;
    private String password;
}
