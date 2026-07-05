package io.github.monalisamenezes.devbank.kafka.producer;

import io.github.monalisamenezes.devbank.kafka.event.NotificationsReadyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TransferNotificationListener {

    private final NotificationProducer notificationProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCompleted(NotificationsReadyEvent event) {
        event.notifications().forEach(notificationProducer::sendTransferNotification);
    }
}
