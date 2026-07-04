package io.github.monalisamenezes.devbank.repository;


import io.github.monalisamenezes.devbank.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
