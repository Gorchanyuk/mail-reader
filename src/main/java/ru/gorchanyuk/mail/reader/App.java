package ru.gorchanyuk.mail.reader;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.gorchanyuk.mail.reader.service.MailReceiverService;
import ru.gorchanyuk.mail.reader.service.MessageProducer;
import ru.gorchanyuk.mail.reader.model.AppealDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class App {

    private final MailReceiverService mailReceiverService;
    private final MessageProducer producer;

    @Scheduled(cron = "${mail.check.interval}")
    public void test(){
        List<AppealDto> appeals = mailReceiverService.getAppeals();
        for (AppealDto appeal : appeals){
            producer.sendMessage(appeal);
        }
    }
}
