package br.com.donus.account.exception;

public class EntityBadRequestException extends RuntimeException {

    EntityBadRequestException(String message) {
        super(message);
    }
}
