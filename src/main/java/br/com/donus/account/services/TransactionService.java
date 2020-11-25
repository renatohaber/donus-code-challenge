package br.com.donus.account.services;

public class TransactionService {

    private final TransactionService transactionRepository;

    public TransactionService(TransactionService transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
}
