package ru.gorchanyuk.mail.reader.util;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gorchanyuk.mail.reader.exception.GetStoreException;
import ru.gorchanyuk.mail.reader.prop.MailProperties;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreUtil {

    private final Session session;
    private final MailProperties properties;

    public Store getStore() {
        Store store;
        try {
            store = session.getStore();
        } catch (MessagingException e) {
            log.error("Ошибка при получении объекта класса Store для хоста: {} и пользователя: {}. Сообщение ошибки: {}",
                    properties.getHost(),
                    properties.getUser(),
                    e.getMessage());
            //TODO устранить ошибки при неработающем интернете
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return getStore();
        }
        try {
            store.connect(properties.getHost(), properties.getPort(), properties.getUser(), properties.getPassword());
        } catch (MessagingException e) {
            log.error("Ошибка при попытке соединения с хранилещем сообщений хоста: {} и пользователя: {}." +
                            "Сообщение об ошибке: {}",
                    properties.getHost(),
                    properties.getUser(),
                    e.getMessage());
            throw new GetStoreException(e.getMessage());
        }
        log.info("Объект класса Store для хоста: {} и пользователя: {} успешно получен",
                properties.getHost(),
                properties.getUser());

        return store;
    }
}
