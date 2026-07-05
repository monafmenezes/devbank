package io.github.monalisamenezes.devbank.kafka.producer;

import io.github.monalisamenezes.devbank.kafka.event.TransferNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static io.github.monalisamenezes.devbank.kafka.config.KafkaTopicConfig.TRANSFER_NOTIFICATIONS_TOPIC;

@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, TransferNotificationEvent> kafkaTemplate;

    public void sendTransferNotification(TransferNotificationEvent event) {
        kafkaTemplate.send(TRANSFER_NOTIFICATIONS_TOPIC, event.accountId().toString(), event);
    }
}
