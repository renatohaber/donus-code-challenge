package br.com.donus.account.test.config.transformer;

import br.com.donus.account.data.entities.Account;
import br.com.donus.account.test.steps.common.EntitySampleFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class AccountTableTransformer extends EnitityTableTransformer<Account> {

    public AccountTableTransformer(ObjectMapper objectMapper, EntitySampleFactory entitySampleFactory) {
        super(objectMapper, entitySampleFactory);
    }

    @Override
    public final Account transform(Map<String, String> entry) throws Throwable {
        return super.transform(entry);
    }
}