package io.github.monalisamenezes.devbank.service;

import io.github.monalisamenezes.devbank.dto.request.AccountRequest;
import io.github.monalisamenezes.devbank.dto.response.AccountResponse;

public interface AccountService {

    AccountResponse create(AccountRequest request);

    AccountResponse findById(Long id);
}
