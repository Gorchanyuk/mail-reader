package ru.gorchanyuk.mail.reader.util;

import jakarta.annotation.PostConstruct;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.gorchanyuk.mail.reader.prop.FolderProperties;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateNewFolderRead {

    private final StoreUtil storeUtil;
    private final FolderProperties folderProperties;

    @PostConstruct
    private void createNewFolder() throws MessagingException {
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
