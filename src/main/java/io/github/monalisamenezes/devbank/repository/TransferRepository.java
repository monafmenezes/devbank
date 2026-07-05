package io.github.monalisamenezes.devbank.repository;

import io.github.monalisamenezes.devbank.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByOriginAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(
            Long originAccountId, Long destinationAccountId);
}
