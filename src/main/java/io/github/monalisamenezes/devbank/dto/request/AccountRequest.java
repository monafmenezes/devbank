package io.github.monalisamenezes.devbank.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountRequest(
        @NotNull Long clientId,
        @NotNull @DecimalMin(value = "0.00") BigDecimal initialBalance
) {
}
