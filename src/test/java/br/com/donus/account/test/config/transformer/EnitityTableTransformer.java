package br.com.donus.account.test.config.transformer;

import br.com.donus.account.test.steps.common.EntitySampleFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.TableEntryTransformer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class EnitityTableTransformer<T> implements TableEntryTransformer<T> {

    protected final ObjectMapper objectMapper;
    protected final EntitySampleFactory entitySampleFactory;
    private final Type type;

    protected EnitityTableTransformer(ObjectMapper objectMapper, EntitySampleFactory entitySampleFactory) {
        this.objectMapper = objectMapper;
        this.entitySampleFactory = entitySampleFactory;
        this.type = this.loadType();
    }

    private Type loadType() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }

        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public T transform(Map<String, String> entry) throws Throwable {
        return this.objectMapper.readValue(this.objectMapper.writeValueAsString(entry), (Class<T>) this.type);
    }

    protected boolean isFilledVariable(String variableValue) {
        return StringUtils.isNotBlank(variableValue) && !StringUtils.equalsIgnoreCase("null", variableValue);
    }
}