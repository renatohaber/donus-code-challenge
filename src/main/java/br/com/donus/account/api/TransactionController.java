package br.com.donus.account.api;

import br.com.donus.account.data.dto.transaction.TransactionResponse;
import br.com.donus.account.services.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(path = "/transactions/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TransactionResponse> findTransactionsByPeriod(@PathVariable("accountId") UUID accountId,
                                                              @RequestParam(value = "startDate", required = false)
                                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                      LocalDate startDate,
                                                              @RequestParam(value = "endDate", required = false)
                                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                      LocalDate endDate) {

        return this.transactionService.findTransactionsByPeriod(accountId, startDate, endDate);
    }

}
