package io.github.monalisamenezes.devbank.service.impl;

import io.github.monalisamenezes.devbank.dto.request.AccountRequest;
import io.github.monalisamenezes.devbank.dto.response.AccountResponse;
import io.github.monalisamenezes.devbank.entity.Account;
import io.github.monalisamenezes.devbank.entity.Client;
import io.github.monalisamenezes.devbank.exception.AccountNotFoundException;
import io.github.monalisamenezes.devbank.exception.ClientNotFoundException;
import io.github.monalisamenezes.devbank.mapper.AccountMapper;
import io.github.monalisamenezes.devbank.repository.AccountRepository;
import io.github.monalisamenezes.devbank.repository.ClientRepository;
import io.github.monalisamenezes.devbank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountResponse create(AccountRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ClientNotFoundException(request.clientId()));

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .balance(request.initialBalance())
                .client(client)
                .build();

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse findById(Long id) {
        return accountMapper.toResponse(
                accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id)));
    }

    private String generateAccountNumber() {
        return String.format("%06d-%d", RANDOM.nextInt(1_000_000), RANDOM.nextInt(10));
    }
}
