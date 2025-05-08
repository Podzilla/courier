package com.podzilla.courier.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_SHIPPED_QUEUE = "order.shipped.queue";
    public static final String ORDER_DELIVERED_QUEUE = "order.delivered.queue";
    public static final String ORDER_FAILED_QUEUE = "order.failed.queue";
    public static final String ORDER_SHIPPED_KEY = "order.shipped";
    public static final String ORDER_DELIVERED_KEY = "order.delivered";
    public static final String ORDER_FAILED_KEY = "order.failed";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderShippedQueue() {
        return new Queue(ORDER_SHIPPED_QUEUE, true);
    }

    @Bean
    public Queue orderDeliveredQueue() {
        return new Queue(ORDER_DELIVERED_QUEUE, true);
    }

    @Bean
    public Queue orderFailedQueue() {
        return new Queue(ORDER_FAILED_QUEUE, true);
    }

    @Bean
    public Binding orderShippedBinding(Queue orderShippedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderShippedQueue).to(orderExchange).with(ORDER_SHIPPED_KEY);
    }

    @Bean
    public Binding orderDeliveredBinding(Queue orderDeliveredQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderDeliveredQueue).to(orderExchange).with(ORDER_DELIVERED_KEY);
    }

    @Bean
    public Binding orderFailedBinding(Queue orderFailedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderFailedQueue).to(orderExchange).with(ORDER_FAILED_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}