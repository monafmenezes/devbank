package io.github.monalisamenezes.devbank.service.impl;

import io.github.monalisamenezes.devbank.dto.request.AccountRequest;
import io.github.monalisamenezes.devbank.dto.response.AccountResponse;
import io.github.monalisamenezes.devbank.entity.Account;
import io.github.monalisamenezes.devbank.entity.Client;
import io.github.monalisamenezes.devbank.exception.AccountNotFoundException;
import io.github.monalisamenezes.devbank.exception.ClientNotFoundException;
import io.github.monalisamenezes.devbank.mapper.AccountMapperImpl;
import io.github.monalisamenezes.devbank.repository.AccountRepository;
import io.github.monalisamenezes.devbank.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl(accountRepository, clientRepository, new AccountMapperImpl());
    }

    @Test
    void deveCriarContaComSaldoInicialParaClienteExistente() {
        Client client = Client.builder().id(1L).name("Monalisa Menezes").build();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return account;
        });

        AccountResponse response = accountService.create(new AccountRequest(1L, new BigDecimal("100.00")));

        assertThat(response.clientId()).isEqualTo(1L);
        assertThat(response.balance()).isEqualByComparingTo("100.00");
    }

    @Test
    void deveLancarExcecaoAoCriarContaParaClienteInexistente() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.create(new AccountRequest(99L, BigDecimal.TEN)))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoAoConsultarContaInexistente() {
        when(accountRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(5L))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
