package io.github.monalisamenezes.devbank.kafka.consumer;

import io.github.monalisamenezes.devbank.kafka.event.TransferNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static io.github.monalisamenezes.devbank.kafka.config.KafkaTopicConfig.TRANSFER_NOTIFICATIONS_TOPIC;

@Slf4j
@Component
public class NotificationConsumer {

    @KafkaListener(topics = TRANSFER_NOTIFICATIONS_TOPIC, groupId = "notification-service")
    public void consume(TransferNotificationEvent event) {
        log.info("Notificação enviada para {} (cliente {}): {}",
                event.clientName(), event.clientId(), event.message());
    }
}
