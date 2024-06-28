package ru.gorchanyuk.mail.reader.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gorchanyuk.mail.reader.prop.RabbitMQProperties;


@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig{

    private final RabbitMQProperties properties;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(properties.getExchange());
    }

    @Bean
    public Queue queue() {
        return new Queue(properties.getQueue(), true);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(properties.getRoutingkey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
    }

//    @Bean
//    public ObjectMapper getObjectMapper(){
//        return new ObjectMapper();
//    }
}