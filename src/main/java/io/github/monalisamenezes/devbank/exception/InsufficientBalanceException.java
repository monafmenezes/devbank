package io.github.monalisamenezes.devbank.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long accountId) {
        super("Saldo insuficiente na conta: " + accountId);
    }
}
