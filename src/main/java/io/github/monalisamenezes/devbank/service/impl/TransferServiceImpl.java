package io.github.monalisamenezes.devbank.service.impl;

import io.github.monalisamenezes.devbank.dto.request.TransferRequest;
import io.github.monalisamenezes.devbank.dto.response.TransferResponse;
import io.github.monalisamenezes.devbank.entity.Account;
import io.github.monalisamenezes.devbank.entity.Transfer;
import io.github.monalisamenezes.devbank.enums.TransferStatus;
import io.github.monalisamenezes.devbank.exception.AccountNotFoundException;
import io.github.monalisamenezes.devbank.exception.InsufficientBalanceException;
import io.github.monalisamenezes.devbank.exception.InvalidTransferException;
import io.github.monalisamenezes.devbank.kafka.event.NotificationsReadyEvent;
import io.github.monalisamenezes.devbank.kafka.event.TransferNotificationEvent;
import io.github.monalisamenezes.devbank.mapper.TransferMapper;
import io.github.monalisamenezes.devbank.repository.AccountRepository;
import io.github.monalisamenezes.devbank.repository.TransferRepository;
import io.github.monalisamenezes.devbank.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        if (request.originAccountId().equals(request.destinationAccountId())) {
            throw new InvalidTransferException("Conta de origem e destino não podem ser iguais");
        }
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("O valor da transferência deve ser maior que zero");
        }

        // trava as contas em ordem crescente de id para evitar deadlock
        Long firstId = Math.min(request.originAccountId(), request.destinationAccountId());
        Long secondId = Math.max(request.originAccountId(), request.destinationAccountId());

        Account first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new AccountNotFoundException(firstId));
        Account second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new AccountNotFoundException(secondId));

        boolean firstIsOrigin = firstId.equals(request.originAccountId());
        Account origin = firstIsOrigin ? first : second;
        Account destination = firstIsOrigin ? second : first;

        if (origin.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException(origin.getId());
        }

        origin.setBalance(origin.getBalance().subtract(request.amount()));
        destination.setBalance(destination.getBalance().add(request.amount()));

        accountRepository.save(origin);
        accountRepository.save(destination);

        Transfer transfer = Transfer.builder()
                .amount(request.amount())
                .status(TransferStatus.COMPLETED)
                .createdAt(Instant.now())
                .originAccount(origin)
                .destinationAccount(destination)
                .build();

        Transfer saved = transferRepository.save(transfer);

        publishNotifications(saved, origin, destination);

        return transferMapper.toResponse(saved);
    }

    @Override
    public List<TransferResponse> findByAccountId(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        return transferRepository
                .findByOriginAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(accountId, accountId)
                .stream()
                .map(transferMapper::toResponse)
                .toList();
    }

    private void publishNotifications(Transfer transfer, Account origin, Account destination) {
        TransferNotificationEvent debited = new TransferNotificationEvent(
                transfer.getId(),
                origin.getClient().getId(),
                origin.getClient().getName(),
                origin.getId(),
                transfer.getAmount(),
                "Transferência de R$ %.2f enviada para a conta %s"
                        .formatted(transfer.getAmount(), destination.getAccountNumber()));

        TransferNotificationEvent credited = new TransferNotificationEvent(
                transfer.getId(),
                destination.getClient().getId(),
                destination.getClient().getName(),
                destination.getId(),
                transfer.getAmount(),
                "Você recebeu uma transferência de R$ %.2f da conta %s"
                        .formatted(transfer.getAmount(), origin.getAccountNumber()));

        eventPublisher.publishEvent(new NotificationsReadyEvent(List.of(debited, credited)));
    }
}
