package io.github.monalisamenezes.devbank.controller;

import io.github.monalisamenezes.devbank.dto.request.AccountRequest;
import io.github.monalisamenezes.devbank.dto.response.AccountResponse;
import io.github.monalisamenezes.devbank.dto.response.TransferResponse;
import io.github.monalisamenezes.devbank.service.AccountService;
import io.github.monalisamenezes.devbank.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Contas")
public class AccountController {

    private final AccountService accountService;
    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Cria uma conta para um cliente com saldo inicial")
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta os dados de uma conta")
    public ResponseEntity<AccountResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/{id}/transfers")
    @Operation(summary = "Consulta as movimentações (transferências) de uma conta")
    public ResponseEntity<List<TransferResponse>> findTransfers(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.findByAccountId(id));
    }
}
