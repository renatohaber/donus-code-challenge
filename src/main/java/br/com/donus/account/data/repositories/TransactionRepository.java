package br.com.donus.account.data.repositories;

import br.com.donus.account.data.entities.Transaction;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@Profile("!test")
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, PagingAndSortingRepository<Transaction, UUID> {

    Stream<Transaction> findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(UUID accountId, LocalDateTime from, LocalDateTime to);

}
