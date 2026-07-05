package io.github.monalisamenezes.devbank.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long clientId) {
        super("Cliente não encontrado: " + clientId);
    }
}
