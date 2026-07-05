package io.github.monalisamenezes.devbank.kafka.event;

import java.math.BigDecimal;

public record TransferNotificationEvent(
        Long transferId,
        Long clientId,
        String clientName,
        Long accountId,
        BigDecimal amount,
        String message
) {
}
