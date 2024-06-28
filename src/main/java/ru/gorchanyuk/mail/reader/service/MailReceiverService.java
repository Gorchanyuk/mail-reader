package ru.gorchanyuk.mail.reader.service;

import ru.gorchanyuk.mail.reader.model.AppealDto;

import java.util.List;

public interface MailReceiverService {

    List<AppealDto> getAppeals();
}
