package br.com.donus.account.test.steps.transaction;

import br.com.donus.account.data.entities.Account;
import br.com.donus.account.data.entities.Transaction;
import br.com.donus.account.data.repositories.TransactionRepository;
import br.com.donus.account.test.AccountApplicationTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;

public class TransactionSteps {

    private final AccountApplicationTestData testData;
    private final TransactionRepository transactionRepository;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TransactionSteps(AccountApplicationTestData testData, TransactionRepository transactionRepository, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.testData = testData;
        this.transactionRepository = transactionRepository;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.testData.reset();
    }

    @After
    public void wrapUp() {
        reset(transactionRepository);
    }

    @Given("database contains transactions as:")
    public void transactionsContainsColumnsAs(List<Transaction> transactions) {
        given(this.transactionRepository.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                any(UUID.class), any(), any())).willReturn(Stream.empty());
        }
}
