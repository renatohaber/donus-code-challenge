package br.com.donus.account.data.repositories;

import br.com.donus.account.data.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>, PagingAndSortingRepository<Transaction, UUID> {
}
