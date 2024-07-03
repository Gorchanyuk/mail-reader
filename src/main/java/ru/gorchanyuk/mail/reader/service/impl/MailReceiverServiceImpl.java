package ru.gorchanyuk.mail.reader.service.impl;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.gorchanyuk.mail.reader.model.AppealDto;
import ru.gorchanyuk.mail.reader.prop.FolderProperties;
import ru.gorchanyuk.mail.reader.service.AttachmentService;
import ru.gorchanyuk.mail.reader.service.DecoderService;
import ru.gorchanyuk.mail.reader.service.MailReceiverService;
import ru.gorchanyuk.mail.reader.service.ProxyMail;
import ru.gorchanyuk.mail.reader.util.ContentType;
import ru.gorchanyuk.mail.reader.util.StoreUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailReceiverServiceImpl implements MailReceiverService {

    @Value("${dispatch.group}")
    private String group;
    private final StoreUtil storeUtil;
    private final FolderProperties folderProperties;
    private final AttachmentService attachmentService;
    private final ExtractStringServiceImpl extractStringServiceImpl;

    @Override
    public List<AppealDto> getAppeals() {

        List<AppealDto> appealDtoList = new ArrayList<>();

        try (Store store = storeUtil.getStore();
             Folder inboxFolder = store.getFolder(folderProperties.getInbox());
             Folder readFolder = store.getFolder(folderProperties.getReadbox())
        ) {
            log.info("Проверка сообщений в папке: {}", folderProperties.getInbox());
            inboxFolder.open(Folder.READ_WRITE);
            readFolder.open(Folder.READ_WRITE);
            Message[] messages = inboxFolder.getMessages();
            for (Message message : messages) {
                appealDtoList.add(getAppealDtoFromMessage(message));
                ProxyMail.changeFlag(message, Flags.Flag.SEEN);
            }
            moveMessagesToAnotherFolder(inboxFolder, readFolder);
        } catch (MessagingException e) {
            log.error("Ошибка при попытке получения новых сообщений в папке: {}. Сообщение об ошибке: {}",
                    folderProperties.getInbox(),
                    e.getMessage());
            throw new RuntimeException(e);
        }
        return appealDtoList;
    }

    private AppealDto getAppealDtoFromMessage(Message message) {

        String REGEX = "^RE:?\\s*(.*)$";
        String emailAddress = extractEmail(ProxyMail.getFrom(message));
        String subject = ProxyMail.getSubject(message);
        return AppealDto.builder()
                .groups(group)
                .theme(extractStringServiceImpl.extractString(subject, REGEX))
                .createdBy(emailAddress)
                .created(LocalDateTime.ofInstant(ProxyMail.getReceivedDate(message).toInstant(), ZoneId.systemDefault()))
                .text(getTextFromMessage(message))
                .isQuestion(ObjectUtils.isEmpty(subject) || !subject.matches(REGEX))
                .attachments(attachmentService.getAttachments(message, emailAddress))
                .build();
    }

    private String getTextFromMessage(Part part) {

        if (ProxyMail.isEqualsType(part, ContentType.PLAIN.getValue())) {
            return ProxyMail.getContent(part).toString();
        }
        if (ProxyMail.isEqualsType(part, ContentType.HTML.getValue())) {
            return getTextFromHtmlType(part);
        }
        if (ProxyMail.isEqualsType(part, ContentType.ALTERNATIVE.getValue())) {
            return getTextFromAlternativeType(part);
        }
        if (ProxyMail.isEqualsType(part, ContentType.MULTIPART.getValue())) {
            MimeMultipart mimeMultipart = (MimeMultipart) ProxyMail.getContent(part);
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromAlternativeType(Part part) {
        MimeMultipart mimeMultipart = (MimeMultipart) ProxyMail.getContent(part);
        //парсим первый попавшийся фрагмент, так как все они одинаковые
        for (int i = 0; i < ProxyMail.getCount(mimeMultipart); i++){
            BodyPart bodyPart = ProxyMail.getBodyPart(mimeMultipart, i);
            if(ProxyMail.isEqualsType(bodyPart, ContentType.HTML.getValue())){
                return getTextFromMessage(bodyPart);
            }
        }
        return getTextFromMessage(ProxyMail.getBodyPart(mimeMultipart, 0));
    }

    private String getTextFromHtmlType(Part part) {
        Element body = Jsoup
                .parse(ProxyMail.getContent(part).toString())
                .body();
        return "\n" + body.children().stream()
                .findFirst()
                .orElse(body).text();
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ProxyMail.getCount(mimeMultipart); i++) {
            BodyPart bodyPart = ProxyMail.getBodyPart(mimeMultipart, i);
            if (ProxyMail.isEqualsType(bodyPart, ContentType.ALTERNATIVE.getValue())){
                //Если есть тип "multipart/alternative", значит основное сообщение в нем а все остальное лишняя информация
                return getTextFromMessage(bodyPart);
            }
            String textFromBodyPart = getTextFromMessage(bodyPart);
            result.append(textFromBodyPart);
        }
        return result.toString();
    }

    private String extractEmail(String input) {
        //Получает адрес почты из строки
        String str = DecoderService.decode(input);
        int startIndex = str.indexOf("<");
        int endIndex = str.indexOf(">");
        if (startIndex != -1 && endIndex != -1) {
            return str.substring(startIndex + 1, endIndex);
        }
        return input;
    }

    private void moveMessagesToAnotherFolder(Folder fromFolder, Folder toFolder) {
        //Перемещает сообщения из папки с входящими в папку с прочитанными
        try {
            Message[] messages = fromFolder.getMessages();
            for (Message message : messages) {
                boolean isRead = message.isSet(Flags.Flag.SEEN);
                if (isRead) {
                    fromFolder.copyMessages(new Message[]{message}, toFolder);
                    ProxyMail.changeFlag(message, Flags.Flag.DELETED);
                    log.info("Перенос сообщения от пользователя: {} из папки {} в папку {} успешно выполнен",
                            extractEmail(ProxyMail.getFrom(message)),
                            fromFolder.getName(),
                            toFolder.getName());
                }
            }
        } catch (MessagingException e) {
            log.warn("Произошла ошибка во время переноса сообщений из папки {} в папку {}",
                    fromFolder.getName(),
                    toFolder.getName());
            throw new RuntimeException(e);
        }
    }
}