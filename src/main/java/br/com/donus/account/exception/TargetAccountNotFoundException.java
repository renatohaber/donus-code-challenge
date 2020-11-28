package br.com.donus.account.exception;

import java.util.UUID;

public class TargetAccountNotFoundException extends EntityNotFoundException {

    public TargetAccountNotFoundException(UUID AccountId) {
        super(String.format("Target account not found for the id: %s", AccountId));
    }

    public TargetAccountNotFoundException(String document) {
        super(String.format("Target account not found for the document: %s", document));
    }

}