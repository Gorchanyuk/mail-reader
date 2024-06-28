package ru.gorchanyuk.mail.reader.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Service;
import ru.gorchanyuk.mail.reader.service.MessageProducer;
import ru.gorchanyuk.mail.reader.model.AppealDto;
import ru.gorchanyuk.mail.reader.prop.RabbitMQProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerImpl implements MessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;
    private final MessageConverter messageConverter;

    @Override
    public void sendMessage(AppealDto dto) {

        Message message = messageConverter.toMessage(dto, new MessageProperties());
        message.getMessageProperties().setHeader("__TypeId__", null);
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchange(), rabbitMQProperties.getRoutingkey(), message);
        log.info("Сообщение от пользователя: '{}' успешно отправленно в RabbitMQ", dto.getCreatedBy());
    }
}