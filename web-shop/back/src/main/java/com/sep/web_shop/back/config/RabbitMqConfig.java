package com.sep.web_shop.back.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${app.rabbitmq.payment-notifications.exchange}")
    String paymentNotificationsExchange;

    @Value("${app.rabbitmq.payment-notifications.queue}")
    String paymentNotificationsQueue;

    @Value("${app.rabbitmq.payment-notifications.routing-key}")
    String paymentNotificationsRoutingKey;

    @Bean
    public DirectExchange paymentNotificationsExchange() {
        return new DirectExchange(
                paymentNotificationsExchange,
                true,
                false
        );
    }

    @Bean
    public Queue paymentNotificationsQueue() {
        return new Queue(
                paymentNotificationsQueue,
                true
        );
    }

    @Bean
    public Binding paymentNotificationsBinding(
            Queue paymentNotificationsQueue,
            DirectExchange paymentNotificationsExchange
    ) {
        return BindingBuilder
                .bind(paymentNotificationsQueue)
                .to(paymentNotificationsExchange)
                .with(paymentNotificationsRoutingKey);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory paymentNotificationsListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);

        return factory;
    }

    @Bean
    public ApplicationRunner declarePaymentNotificationsRabbitResources(
            AmqpAdmin amqpAdmin,
            DirectExchange paymentNotificationsExchange,
            Queue paymentNotificationsQueue,
            Binding paymentNotificationsBinding
    ) {
        return args -> {
            amqpAdmin.declareExchange(paymentNotificationsExchange);
            amqpAdmin.declareQueue(paymentNotificationsQueue);
            amqpAdmin.declareBinding(paymentNotificationsBinding);
        };
    }

}