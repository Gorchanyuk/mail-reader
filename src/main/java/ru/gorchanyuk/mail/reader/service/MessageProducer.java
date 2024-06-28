package ru.gorchanyuk.mail.reader.service;

import ru.gorchanyuk.mail.reader.model.AppealDto;

public interface MessageProducer {

    void sendMessage(AppealDto dto);
}
