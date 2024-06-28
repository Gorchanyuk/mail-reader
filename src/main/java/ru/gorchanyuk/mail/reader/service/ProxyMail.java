package ru.gorchanyuk.mail.reader.service;

import jakarta.mail.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
@Slf4j
public
class ProxyMail {
    //Класс прокся. Все методы используются для обработки исключений MessagingException в одном месте.

    public static boolean isEqualsType(Part part, String type) {
        try {
            return part.isMimeType(type);
        } catch (MessagingException e) {
            log.error("Произошла ошибка при проверке соответствия типа сообщения {}, типу {}",
                    getDescription(part),
                    type);
            throw new RuntimeException(e);
        }
    }

    public static void changeFlag(Message message, Flags.Flag flag) {
        try {
            if (!message.isSet(flag)) {
                message.setFlag(flag, true);
            }
        } catch (MessagingException e) {
            log.warn("Произошла ошибка при попытке изменения флага в методе changeFlag(Message message, Flag flag)");
            throw new RuntimeException(e);
        }
    }

    public static Object getContent(Part part) {
        try {
            return part.getContent();
        } catch (IOException | MessagingException e) {
            log.error("Произошла ошибка при извлечении контента из сообщения: {}",
                    getDescription(part));
            throw new RuntimeException(e);
        }
    }

    public static String getDescription(Part part) {
        try {
            return part.getDescription();
        } catch (MessagingException e) {
            log.error("Произошла ошибка во время обработки исключения в методе ProxyMail.getDescription(Part part)");
            throw new RuntimeException(e);
        }
    }

    public static String getFileName(Part part) {
        try {
            return part.getFileName();
        } catch (MessagingException e) {
            log.error("Произошла ошибка во время извлечения названия файла в части сообщения: {}",
                    getDescription(part));
            throw new RuntimeException(e);
        }
    }

    public static String getDisposition(Part part) {
        try {
            return part.getDisposition();
        } catch (MessagingException e) {
            log.error("Произошла ошибка во время извлечения Disposition части сообщения: {}",
                    getDescription(part));
            throw new RuntimeException(e);
        }
    }

    public static InputStream getInputStream(Part part) throws IOException {
        try {
            return part.getInputStream();
        } catch (MessagingException e) {
            log.error("Произошла ошибка во время извлечения InputStream в части сообщения: {}",
                    getDescription(part));
            throw new RuntimeException(e);
        }
    }

    public static String getFrom(Message message) {
        try {
            return message.getFrom()[0].toString();
        } catch (MessagingException e) {
            log.error("Произошла ошибка при извлечении отправителя из сообщения: {}",
                    getDescription(message));
            throw new RuntimeException(e);
        }
    }

    public static String getSubject(Message message) {
        try {
            return message.getSubject();
        } catch (MessagingException e) {
            log.error("Произошла ошибка при извлечении темы сообщения из сообщения: {}",
                    getDescription(message));
            throw new RuntimeException(e);
        }
    }

    public static Date getReceivedDate(Message message) {
        try {
            return message.getReceivedDate();
        } catch (MessagingException e) {
            log.error("Произошла ошибка при извлечении даты отправления сообщения из сообщения: {}",
                    getDescription(message));
            throw new RuntimeException(e);
        }
    }

    public static int getCount(Multipart multipart) {
        try {
            return multipart.getCount();
        } catch (MessagingException e) {
            log.error("Произошла ошибка при извлечении количества частей в сообщении, в методе ProxyMail.getCount(Multipart multipart)");
            throw new RuntimeException(e);
        }
    }

    public static BodyPart getBodyPart(Multipart multipart, int i) {
        try {
            return multipart.getBodyPart(i);
        } catch (MessagingException e) {
            log.error("Произошла ошибка при извлечении части сообщения, в методе ProxyMail.getBodyPart(Multipart multipart, int i)");
            throw new RuntimeException(e);
        }
    }
}
