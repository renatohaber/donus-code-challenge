package br.com.donus.account.data.mappers.common;

import br.com.donus.account.data.dto.common.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Mapper(componentModel = "spring")
public class PageMapper {

    public <T> PageResponse<T> sourceToTarget(Page<T> page) {
        Pageable pageable = page.getPageable();
        return PageResponse.<T>builder()
                .items(page.getContent())
                .page(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .total(page.getTotalElements())
                .build();
    }

}