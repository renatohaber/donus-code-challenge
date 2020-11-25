package br.com.donus.account.exception;

public class InvalidTaxIdException extends EntityBadRequestException {

    private static final String MESSAGE_TEMPLATE = "Invalid Tax Id: %s";

    public InvalidTaxIdException(String taxId) {
        super(String.format(MESSAGE_TEMPLATE, taxId));
    }

}