package io.github.monalisamenezes.devbank.dto.response;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        Long clientId,
        String clientName
) {
}
