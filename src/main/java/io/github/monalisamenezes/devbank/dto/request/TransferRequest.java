package io.github.monalisamenezes.devbank.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull Long originAccountId,
        @NotNull Long destinationAccountId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount
) {
}
