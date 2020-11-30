package br.com.donus.account.test.steps.transaction;

import br.com.donus.account.data.dto.transaction.TransactionResponse;
import br.com.donus.account.data.entities.Transaction;
import br.com.donus.account.data.repositories.TransactionRepository;
import br.com.donus.account.test.AccountApplicationTestData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
        this.testData.reset();
    }

    @After
    public void wrapUp() {
        reset(transactionRepository);
    }

    @Given("database contains transactions as:")
    public void databaseContainsTransactionsAs(List<Transaction> transactions) {
        given(this.transactionRepository.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(
                any(UUID.class), any(), any())).willReturn(Stream.empty());

        transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getAccountId))
                .forEach((account, proprietaryTransactions) -> given(this.transactionRepository.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(eq(account), any(), any()))
                        .willAnswer(
                                (Answer<Stream<Transaction>>) invocation -> proprietaryTransactions.stream()));
    }

    @Given("the next transaction search data is:")
    public void theNextTransactionSearchDataIs(Map<String, String> transactionSearchData) {
        final LocalDateTime now = LocalDateTime.parse(transactionSearchData.get("now"), DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss z"));
        this.testData.setNow(now);
    }

    @When("this user requests the transactions for the account {string}:")
    public void thisUserRequestsTheTransactionsForTheAccount(String accountId, Map<String, String> parameters) {

        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.get("/transactions/" + accountId)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON);

        parameters.forEach(mockHttpServletRequestBuilder::queryParam);

        ResultActions resultActions = null;
        try {
            resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
        } catch (Exception e) {
            this.testData.setException(e);
        }

        this.testData.setResultActions(resultActions);
    }

    @And("the service will reply this list of transactions: {string}")
    public void theServiceWillReplyThisListOfTransactions(String expectedJson) throws Exception {

        final TypeReference<List<TransactionResponse>> listTransactionTypeReference = new TypeReference<>() {
        };
        List<TransactionResponse> expectedResponse = objectMapper.readValue(expectedJson, listTransactionTypeReference);

        MvcResult mvcResult = this.testData.getResultActions()
                .andReturn();
        final String json = mvcResult.getResponse()
                .getContentAsString();

        List<TransactionResponse> response = objectMapper.readValue(json, listTransactionTypeReference);

        Assert.assertEquals(expectedResponse, response);
    }
}
