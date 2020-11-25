package br.com.donus.account.data.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sun.istack.NotNull;
import lombok.*;

import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "AccountRequestBuilder", toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AccountRequest.AccountRequestBuilder.class)
public class AccountRequest {

    @NotNull
    @Size(min = 3, max = 255)
    private final String name;

    @NotNull
    private final String taxId;

    private final BigDecimal balance;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AccountRequestBuilder {

    }
}