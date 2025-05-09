package com.podzilla.courier.events;

import com.podzilla.courier.config.RabbitMQConfig;
import com.podzilla.courier.dtos.events.OrderDeliveredEvent;
import com.podzilla.courier.dtos.events.OrderFailedEvent;
import com.podzilla.courier.dtos.events.OrderShippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQEventPublisher implements EventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishOrderShipped(OrderShippedEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_SHIPPED_KEY, event, message -> {
                message.getMessageProperties().setCorrelationId(java.util.UUID.randomUUID().toString());
                return message;
            });
            logger.info("Published order.shipped event for order ID: {}", event.orderId());
        } catch (Exception e) {
            logger.error("Failed to publish order.shipped event for order ID: {}", event.orderId(), e);
        }
    }

    @Override
    public void publishOrderDelivered(OrderDeliveredEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_DELIVERED_KEY, event, message -> {
                message.getMessageProperties().setCorrelationId(java.util.UUID.randomUUID().toString());
                return message;
            });
            logger.info("Published order.delivered event for order ID: {}", event.orderId());
        } catch (Exception e) {
            logger.error("Failed to publish order.delivered event for order ID: {}", event.orderId(), e);
        }
    }

    @Override
    public void publishOrderFailed(OrderFailedEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_FAILED_KEY, event, message -> {
                message.getMessageProperties().setCorrelationId(java.util.UUID.randomUUID().toString());
                return message;
            });
            logger.info("Published order.failed event for order ID: {}", event.orderId());
        } catch (Exception e) {
            logger.error("Failed to publish order.failed event for order ID: {}", event.orderId(), e);
        }
    }
}