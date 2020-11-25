package br.com.donus.account.data.repositories;

import br.com.donus.account.data.entities.Account;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("!test")
public interface AccountRepository extends JpaRepository<Account, UUID>, PagingAndSortingRepository<Account, UUID> {

    Optional<Account> findAccountByTaxId(String taxId);

    Optional<Account> findAccountById(UUID id);
}