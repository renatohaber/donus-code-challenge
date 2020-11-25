package br.com.donus.account.data.mappers;

import br.com.donus.account.data.dto.transaction.TransactionResponse;
import br.com.donus.account.data.entities.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionResponse toTransactionPeriodEntryResponse(Transaction transaction);

}
