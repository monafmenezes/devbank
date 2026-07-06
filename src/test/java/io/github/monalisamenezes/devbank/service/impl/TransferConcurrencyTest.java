package io.github.monalisamenezes.devbank.service.impl;

import io.github.monalisamenezes.devbank.dto.request.AccountRequest;
import io.github.monalisamenezes.devbank.dto.request.TransferRequest;
import io.github.monalisamenezes.devbank.dto.response.AccountResponse;
import io.github.monalisamenezes.devbank.entity.Account;
import io.github.monalisamenezes.devbank.repository.AccountRepository;
import io.github.monalisamenezes.devbank.service.AccountService;
import io.github.monalisamenezes.devbank.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TransferConcurrencyTest {

    private static final int THREADS = 50;
    private static final BigDecimal AMOUNT_PER_TRANSFER = new BigDecimal("10.00");

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void deveManterConsistenciaDeSaldoComTransferenciasConcorrentes() throws InterruptedException {
        AccountResponse origin = accountService.create(new AccountRequest(1L, new BigDecimal("100000.00")));
        AccountResponse destination = accountService.create(new AccountRequest(2L, BigDecimal.ZERO));

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch readyLatch = new CountDownLatch(THREADS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(THREADS);
        AtomicInteger failures = new AtomicInteger();

        IntStream.range(0, THREADS).forEach(i -> executor.submit(() -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                transferService.transfer(new TransferRequest(origin.id(), destination.id(), AMOUNT_PER_TRANSFER));
            } catch (Exception e) {
                failures.incrementAndGet();
            } finally {
                doneLatch.countDown();
            }
        }));

        readyLatch.await();
        startLatch.countDown();
        boolean finished = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(finished).isTrue();
        assertThat(failures.get()).isZero();

        Account originAfter = accountRepository.findById(origin.id()).orElseThrow();
        Account destinationAfter = accountRepository.findById(destination.id()).orElseThrow();

        BigDecimal expectedTransferred = AMOUNT_PER_TRANSFER.multiply(BigDecimal.valueOf(THREADS));

        assertThat(originAfter.getBalance())
                .isEqualByComparingTo(new BigDecimal("100000.00").subtract(expectedTransferred));
        assertThat(destinationAfter.getBalance()).isEqualByComparingTo(expectedTransferred);

        List<?> transfers = transferService.findByAccountId(origin.id());
        assertThat(transfers).hasSize(THREADS);
    }
}
