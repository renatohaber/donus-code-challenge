package br.com.donus.account.data.dto.transaction;

import br.com.donus.account.data.entities.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder(builderClassName = "TransactionResponseBuilder", toBuilder = true)
@JsonDeserialize(builder = TransactionResponse.TransactionResponseBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {

    private final TransactionType transactionType;
    private final BigDecimal amount;
    private final LocalDateTime creationDate;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionResponseBuilder {

    }
}