package io.github.monalisamenezes.devbank.kafka.event;

import java.util.List;

public record NotificationsReadyEvent(List<TransferNotificationEvent> notifications) {
}
