package br.com.donus.account.exception;

import java.util.UUID;

public class AccountAlreadyExistsException extends EntityNotFoundException {

    private static final String MESSAGE_TEMPLATE = "Account already exists for the tax id: %s";

    public AccountAlreadyExistsException(UUID AccountId) {
        super(String.format(MESSAGE_TEMPLATE, AccountId));
    }

    public AccountAlreadyExistsException(String document) {
        super(String.format(MESSAGE_TEMPLATE, document));
    }

}