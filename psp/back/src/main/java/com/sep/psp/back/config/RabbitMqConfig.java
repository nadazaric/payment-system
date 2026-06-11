package com.sep.psp.back.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${app.rabbitmq.payment-notifications.exchange}")
    String paymentNotificationsExchange;

    @Bean
    public DirectExchange paymentNotificationsExchange() {
        return new DirectExchange(
                paymentNotificationsExchange,
                true,
                false
        );
    }

    @Bean
    public ApplicationRunner declarePaymentNotificationsExchange(
            AmqpAdmin amqpAdmin,
            DirectExchange paymentNotificationsExchange
    ) {
        return args -> amqpAdmin.declareExchange(paymentNotificationsExchange);
    }

}