package io.github.monalisamenezes.devbank.controller;

import io.github.monalisamenezes.devbank.dto.request.TransferRequest;
import io.github.monalisamenezes.devbank.dto.response.TransferResponse;
import io.github.monalisamenezes.devbank.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Transferências")
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Realiza uma transferência entre duas contas")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.transfer(request));
    }
}
