package io.github.monalisamenezes.devbank.service.impl;

import io.github.monalisamenezes.devbank.dto.request.TransferRequest;
import io.github.monalisamenezes.devbank.dto.response.TransferResponse;
import io.github.monalisamenezes.devbank.entity.Account;
import io.github.monalisamenezes.devbank.entity.Client;
import io.github.monalisamenezes.devbank.entity.Transfer;
import io.github.monalisamenezes.devbank.enums.TransferStatus;
import io.github.monalisamenezes.devbank.exception.AccountNotFoundException;
import io.github.monalisamenezes.devbank.exception.InsufficientBalanceException;
import io.github.monalisamenezes.devbank.exception.InvalidTransferException;
import io.github.monalisamenezes.devbank.kafka.event.NotificationsReadyEvent;
import io.github.monalisamenezes.devbank.mapper.TransferMapperImpl;
import io.github.monalisamenezes.devbank.repository.AccountRepository;
import io.github.monalisamenezes.devbank.repository.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private TransferServiceImpl transferService;

    private Account origin;
    private Account destination;

    @BeforeEach
    void setUp() {
        transferService = new TransferServiceImpl(
                accountRepository, transferRepository, new TransferMapperImpl(), eventPublisher);

        Client originClient = Client.builder().id(1L).name("Monalisa Menezes").build();
        Client destinationClient = Client.builder().id(2L).name("João Silva").build();

        origin = Account.builder()
                .id(1L)
                .accountNumber("0001-1")
                .balance(new BigDecimal("1000.00"))
                .client(originClient)
                .build();

        destination = Account.builder()
                .id(2L)
                .accountNumber("0002-1")
                .balance(new BigDecimal("500.00"))
                .client(destinationClient)
                .build();
    }

    @Test
    void deveTransferirComSucessoEDebitarCreditarAsContas() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(origin));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(destination));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer transfer = invocation.getArgument(0);
            transfer.setId(10L);
            return transfer;
        });

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("200.00"));

        TransferResponse response = transferService.transfer(request);

        assertThat(origin.getBalance()).isEqualByComparingTo("800.00");
        assertThat(destination.getBalance()).isEqualByComparingTo("700.00");
        assertThat(response.status()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(response.amount()).isEqualByComparingTo("200.00");

        ArgumentCaptor<NotificationsReadyEvent> captor = ArgumentCaptor.forClass(NotificationsReadyEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().notifications()).hasSize(2);
    }

    @Test
    void deveRejeitarTransferenciaComSaldoInsuficiente() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(origin));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(destination));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("5000.00"));

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(InsufficientBalanceException.class);

        assertThat(origin.getBalance()).isEqualByComparingTo("1000.00");
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deveRejeitarTransferenciaParaMesmaConta() {
        TransferRequest request = new TransferRequest(1L, 1L, BigDecimal.TEN);

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(InvalidTransferException.class);

        verifyNoInteractions(accountRepository);
    }

    @Test
    void deveRejeitarValorMenorOuIgualAZero() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.ZERO);

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(InvalidTransferException.class);

        verifyNoInteractions(accountRepository);
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoExiste() {
        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.TEN);

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
