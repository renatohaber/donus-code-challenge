package br.com.donus.account.test.config.transformer;

import br.com.donus.account.data.entities.Transaction;
import br.com.donus.account.test.steps.common.EntitySampleFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class TransactionTableTransformer extends EnitityTableTransformer<Transaction> {

    public TransactionTableTransformer(ObjectMapper objectMapper, EntitySampleFactory entitySampleFactory) {
        super(objectMapper, entitySampleFactory);
    }

    @Override
    public final Transaction transform(Map<String, String> entry) throws Throwable {
        return super.transform(entry);
    }
}