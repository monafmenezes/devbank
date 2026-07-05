package io.github.monalisamenezes.devbank.mapper;

import io.github.monalisamenezes.devbank.dto.response.TransferResponse;
import io.github.monalisamenezes.devbank.entity.Transfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(target = "originAccountId", source = "originAccount.id")
    @Mapping(target = "destinationAccountId", source = "destinationAccount.id")
    TransferResponse toResponse(Transfer transfer);
}
