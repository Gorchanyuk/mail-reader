package ru.gorchanyuk.mail.reader.config;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import ru.gorchanyuk.mail.reader.prop.FolderProperties;
import ru.gorchanyuk.mail.reader.util.StoreUtil;

/**
 * Создает папку на почте, в которую будут переноситься сообщения
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class CreateNewFolderConfig {

    private final StoreUtil storeUtil;
    private final FolderProperties folderProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void createNewFolder() throws MessagingException {
        //Создает папку, если она еще не создана, для хранения прочитанных сообщений
        try (Store store = storeUtil.getStore()) {
            Folder newFolder = store.getFolder(folderProperties.getReadbox());
            if (!newFolder.exists()) {
                boolean created = newFolder.create(Folder.HOLDS_MESSAGES);
                if (created) {
                    log.info("Папка {} успешно создана", folderProperties.getReadbox());
                } else {
                    log.error("Не удалось создать папку с именем {}", folderProperties.getReadbox());
                }
            }
        }
    }
}
