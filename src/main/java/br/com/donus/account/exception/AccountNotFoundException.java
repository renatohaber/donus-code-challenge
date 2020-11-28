package br.com.donus.account.exception;

import java.util.UUID;

public class AccountNotFoundException extends EntityNotFoundException {

    public AccountNotFoundException(UUID AccountId) {
        super(String.format("Account not found for the id: %s", AccountId));
    }

    public AccountNotFoundException(String document) {
        super(String.format("Account not found for the document: %s", document));
    }

}