package com.sep.psp.back.config;

import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${app.rabbitmq.payment-notifications.exchange}")
    String paymentNotificationsExchange;

    @Autowired
    AppLoggerService appLoggerService;

    @Bean
    public DirectExchange paymentNotificationsExchange() {
        return new DirectExchange(
                paymentNotificationsExchange,
                true,
                false
        );
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);

        return rabbitTemplate;
    }

    @Bean
    public ApplicationRunner declarePaymentNotificationsExchange(
            AmqpAdmin amqpAdmin,
            DirectExchange paymentNotificationsExchange
    ) {
        return args -> {
            amqpAdmin.declareExchange(paymentNotificationsExchange);

            appLoggerService.info(
                    LogStrings.Feature.APP,
                    LogStrings.Action.RABBITMQ_CONNECTED,
                    "RabbitMQ connection established and payment notifications exchange declared. exchange={}",
                    paymentNotificationsExchange.getName()
            );
        };
    }

}