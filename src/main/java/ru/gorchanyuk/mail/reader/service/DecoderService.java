package ru.gorchanyuk.mail.reader.service;

import jakarta.mail.internet.MimeUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
public
class DecoderService {

    public static String decode (String str){
        // Декодирование строки, если она закодированна
        try {
            log.info("Декодируем строку: {}", str);
            return MimeUtility.decodeText(str);
        } catch (UnsupportedEncodingException e) {
            log.error("Ошибка при попытке декодирования строки: {}", str);
            throw new RuntimeException(e);
        }
    }
}
