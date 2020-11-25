package br.com.donus.account.exception;

import java.math.BigDecimal;

public class NotEnoughFundsException extends EntityBadRequestException {

    private static final String MESSAGE_TEMPLATE = "Not enough founds for this transaction: %s";

    public NotEnoughFundsException(BigDecimal value) {
        super(String.format(MESSAGE_TEMPLATE, value));
    }

}