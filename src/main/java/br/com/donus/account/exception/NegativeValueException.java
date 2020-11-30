package br.com.donus.account.exception;

import java.math.BigDecimal;

public class NegativeValueException extends EntityBadRequestException {

    private static final String MESSAGE_TEMPLATE = "Negative value exception: %s";

    public NegativeValueException(BigDecimal value) {
        super(String.format(MESSAGE_TEMPLATE, value));
    }

}