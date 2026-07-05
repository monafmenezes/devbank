package io.github.monalisamenezes.devbank.service;

import io.github.monalisamenezes.devbank.dto.request.TransferRequest;
import io.github.monalisamenezes.devbank.dto.response.TransferResponse;

import java.util.List;

public interface TransferService {

    TransferResponse transfer(TransferRequest request);

    List<TransferResponse> findByAccountId(Long accountId);
}
