package br.com.donus.account.services;

import br.com.donus.account.data.dto.transaction.TransactionResponse;
import br.com.donus.account.data.entities.Transaction;
import br.com.donus.account.data.mappers.TransactionMapper;
import br.com.donus.account.data.repositories.TransactionRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<TransactionResponse> findTransactionsByPeriod(UUID accountId, LocalDate startDate, LocalDate endDate) {

        Pair<LocalDateTime, LocalDateTime> timeFrame;
        if (startDate == null || endDate == null) {
            timeFrame = this.calculateDefaultTimeFrame();
        } else {
            timeFrame = Pair.of(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay().minusNanos(1));
        }

        Stream<Transaction> transactions =
                transactionRepository.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(accountId,
                        timeFrame.getLeft(), timeFrame.getRight());

        return transactions.map(transactionMapper::toTransactionPeriodEntryResponse)
                .collect(Collectors.toList());
    }

    private Pair<LocalDateTime, LocalDateTime> calculateDefaultTimeFrame() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.minusMonths(12)
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay();

        LocalDateTime end = today.minusMonths(1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(23, 59, 59);

        return Pair.of(start, end);
    }
}
