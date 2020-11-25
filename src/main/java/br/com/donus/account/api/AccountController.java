package br.com.donus.account.api;

import br.com.donus.account.data.dto.account.AccountResponse;
import br.com.donus.account.data.dto.account.CreateAccountRequest;
import br.com.donus.account.data.dto.common.PageResponse;
import br.com.donus.account.services.AccountService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(value = "/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping(value = "/accounts/taxid/{taxid}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse getByDocument(@PathVariable(value = "taxid") String taxid) {
        return accountService.findByTaxId(taxid);
    }

    @GetMapping(value = "/accounts/id/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse getById(@PathVariable(value = "id") UUID id) {
        return accountService.findAccountById(id);
    }

    @GetMapping(value = "/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<AccountResponse> listAll(@SortDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return accountService.listAll(pageable);
    }

    @PutMapping(value = "/accounts/{id}/deposit/{value}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@PathVariable(value = "id") UUID id,
                        @PathVariable(value = "value") BigDecimal value) {
        accountService.deposit(id, value);
    }

    @PutMapping(value = "/accounts/{id}/withdraw/{value}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@PathVariable(value = "id") UUID id,
                         @PathVariable(value = "value") BigDecimal value) {
        accountService.withdraw(id, value);
    }

    @PutMapping(value = "/accounts/{id}/transfer/{target}/{value}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@PathVariable(value = "id") UUID id,
                         @PathVariable(value = "target") UUID target,
                         @PathVariable(value = "value") BigDecimal value) {
        accountService.transfer(id, target, value);
    }

}
