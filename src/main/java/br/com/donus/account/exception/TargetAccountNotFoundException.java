package br.com.donus.account.exception;

import java.util.UUID;

public class TargetAccountNotFoundException extends EntityNotFoundException {

    private static final String MESSAGE_TEMPLATE = "Account not found: %s";

    public TargetAccountNotFoundException(UUID AccountId) {
        super(String.format(MESSAGE_TEMPLATE, AccountId));
    }

    public TargetAccountNotFoundException(String document) {
        super(String.format(MESSAGE_TEMPLATE, document));
    }

}