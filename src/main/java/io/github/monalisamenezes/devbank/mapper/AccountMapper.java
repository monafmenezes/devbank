package io.github.monalisamenezes.devbank.mapper;

import io.github.monalisamenezes.devbank.dto.response.AccountResponse;
import io.github.monalisamenezes.devbank.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    AccountResponse toResponse(Account account);
}
