package io.github.monalisamenezes.devbank.dto.response;

import io.github.monalisamenezes.devbank.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(
        Long id,
        BigDecimal amount,
        TransferStatus status,
        Instant createdAt,
        Long originAccountId,
        Long destinationAccountId
) {
}
