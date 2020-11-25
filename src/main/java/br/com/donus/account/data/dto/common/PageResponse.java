package br.com.donus.account.data.dto.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "PageResponseBuilder", toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    private final long page;

    private final int pageSize;

    private final long total;

    private final List<T> items;

    /*Issue: https://github.com/FasterXML/jackson-databind/issues/921*/
    @JsonCreator
    public static <T> PageResponse<T> create(@JsonProperty("page") long page,
                                             @JsonProperty("pageSize") int pageSize,
                                             @JsonProperty("total") long total,
                                             @JsonProperty("items") List<T> items) {
        return PageResponse.<T>builder().page(page)
                .pageSize(pageSize)
                .total(total)
                .items(items)
                .build();
    }

    public static class PageResponseBuilder<T> {

    }

}
