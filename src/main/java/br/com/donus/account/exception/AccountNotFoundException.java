package br.com.donus.account.exception;

import java.util.UUID;

public class AccountNotFoundException extends EntityNotFoundException {

    private static final String MESSAGE_TEMPLATE = "Account not found for the document: %s";

    public AccountNotFoundException(UUID AccountId) {
        super(String.format(MESSAGE_TEMPLATE, AccountId));
    }

    public AccountNotFoundException(String document) {
        super(String.format(MESSAGE_TEMPLATE, document));
    }

}