package br.com.donus.account.data.mappers;

import br.com.donus.account.data.dto.account.AccountRequest;
import br.com.donus.account.data.dto.account.AccountResponse;
import br.com.donus.account.data.entities.Account;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.TimeZone;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse sourceToTarget(Account account, @Context TimeZone timeZone);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lastUpdate", ignore = true)
    Account targetToSource(AccountRequest request);

    AccountResponse sourceToListTarget(Account account, @Context TimeZone timeZone);
}
