package ru.gorchanyuk.mail.reader.config;

import jakarta.mail.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gorchanyuk.mail.reader.prop.MailProperties;

import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SessionConfig {
    private final MailProperties mailProperties;
    //Создает сессию для чтения сообщений с почты

    @Bean
    public Session getSession(@Qualifier("mailProps") Properties properties){
        Session session = Session.getInstance(properties);
        log.info("Создана сессия для работы с почтой");
        return session;
    }

    @Bean("mailProps")
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", mailProperties.getTransportProtocol());
        properties.setProperty("mail.imaps.host", mailProperties.getHost());
        properties.setProperty("mail.imaps.port", mailProperties.getPort().toString());
        return properties;
    }
}
