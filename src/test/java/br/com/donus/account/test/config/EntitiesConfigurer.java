package br.com.donus.account.test.config;

import br.com.donus.account.data.entities.Account;
import br.com.donus.account.data.entities.Transaction;
import br.com.donus.account.test.config.transformer.AccountTableTransformer;
import br.com.donus.account.test.config.transformer.TransactionTableTransformer;
import br.com.donus.account.test.steps.common.EntitySampleFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;

import java.util.Locale;

@SuppressWarnings("unused")
public class EntitiesConfigurer implements TypeRegistryConfigurer {

    public EntitiesConfigurer() {

    }

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        EntitySampleFactory sampleFactory = new EntitySampleFactory();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        typeRegistry.defineDataTableType(
                new DataTableType(Account.class, new AccountTableTransformer(objectMapper, sampleFactory)));

        typeRegistry.defineDataTableType(
                new DataTableType(Transaction.class, new TransactionTableTransformer(objectMapper, sampleFactory)));

    }
}