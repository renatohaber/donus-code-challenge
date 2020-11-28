package br.com.donus.account.test.steps.account;

import br.com.donus.account.data.dto.account.AccountResponse;
import br.com.donus.account.data.dto.account.CreateAccountRequest;
import br.com.donus.account.data.dto.common.PageResponse;
import br.com.donus.account.data.entities.Account;
import br.com.donus.account.data.repositories.AccountRepository;
import br.com.donus.account.test.AccountApplicationTestData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

public class AccountSteps {

    private final AccountApplicationTestData testData;
    private final AccountRepository accountRepository;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public AccountSteps(AccountApplicationTestData testData, AccountRepository accountRepository, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.testData = testData;
        this.accountRepository = accountRepository;
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
        reset(accountRepository);
    }

    @Given("database contains accounts as:")
    public void databaseContainsAccountsAs(List<Account> accounts) {
        given(this.accountRepository.findAccountById(any(UUID.class))).willReturn(Optional.empty());
        given(this.accountRepository.findAccountByTaxId(anyString())).willReturn(Optional.empty());
        given(this.accountRepository.findAll(any(Pageable.class)))
                .willAnswer((Answer<Page<Account>>) invocation -> Page.empty(invocation.getArgument(1)));
        accounts.forEach(account -> given(this.accountRepository.findAccountById(eq(account.getId()))).willReturn(Optional.of(account)));
        accounts.forEach(account -> given(this.accountRepository.findAccountByTaxId(eq(account.getTaxId()))).willReturn(Optional.of(account)));

        Answer<Page<Account>> findAllAnswer = invocation -> {
            List<Account> selectedAccounts = new ArrayList<>(accounts);

            return new PageImpl<>(selectedAccounts, invocation.getArgument(0), 0);
        };
        given(this.accountRepository.findAll(any(Pageable.class))).will(findAllAnswer);
    }

    @Given("the next account insertion data is:")
    public void theNextAccountInsertionDataIs(Map<String, String> accountInsertionData) {
        final LocalDateTime now = LocalDateTime.parse(accountInsertionData.get("now"), DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss z"));
        this.testData.setNow(now);
        when(this.accountRepository.save(any()))
                .thenAnswer(invocation -> {
                    final Account account = invocation.getArgument(0);

                    account.setId(UUID.fromString(accountInsertionData.get("nextId")));

                    account.setCreationDate(now);
                    account.setLastUpdate(now);

                    return account;
                });

    }

    @When("this user requests details about the account {string}")
    public void thisUserRequestsDetailsAboutThisAccount(String accountId) {
        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.get("/accounts/id/" + accountId)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = null;
        try {
            resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
        } catch (Exception e) {
            this.testData.setException(e);
        }

        this.testData.setResultActions(resultActions);
    }

    @When("this user requests details for all the accounts:")
    public void thisUserRequestsDetailsForAllTheAccounts(Map<String, String> parameters) {
        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.get("/accounts/")
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

    @When("this user requests details about the account by the tax id {string}")
    public void thisUserRequestsDetailsAboutThisAccountByTheTaxId(String taxId) {
        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.get("/accounts/taxid/" + taxId)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = null;
        try {
            resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
        } catch (Exception e) {
            this.testData.setException(e);
        }

        this.testData.setResultActions(resultActions);
    }

    @Then("the service will reply this account: {string}")
    public void theServiceWillReplyThisAccount(String json) throws IOException {
        AccountResponse expectedResponse = objectMapper.readValue(json, AccountResponse.class);

        MvcResult mvcResult = this.testData.getResultActions()
                .andReturn();
        AccountResponse response =
                objectMapper.readValue(mvcResult.getResponse()
                        .getContentAsString(), AccountResponse.class);

        Assert.assertEquals(expectedResponse, response);
    }

    @Then("the service will reply this list of accounts: {string}")
    public void theServiceWillReplyThisListOfAccounts(String expectedJson) throws IOException {
        final TypeReference<PageResponse<AccountResponse>> pageOfAccounts = new TypeReference<>() {
        };
        PageResponse<AccountResponse> expectedResponse = objectMapper.readValue(expectedJson, pageOfAccounts);

        MvcResult mvcResult = this.testData.getResultActions()
                .andReturn();
        final String json = mvcResult.getResponse()
                .getContentAsString();

        PageResponse<AccountResponse> response = objectMapper.readValue(json, pageOfAccounts);

        Assert.assertEquals(expectedResponse, response);
    }

    @When("this user request to insert the account:")
    public void thisUserRequestToInsertTheAccount(Map<String, String> accountRequestMap) {

        CreateAccountRequest createAccountRequest = null;
        try {
            createAccountRequest = CreateAccountRequest.builder()
                    .name(accountRequestMap.get("name"))
                    .taxId(accountRequestMap.get("taxId"))
                    .build();
        } catch (Exception ignored) {
        }


        ResultActions resultActions = null;
        try {
            MockHttpServletRequestBuilder
                    mockHttpServletRequestBuilder =
                    MockMvcRequestBuilders.post("/accounts/")
                            .with(csrf())
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(createAccountRequest));
            resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
        } catch (Exception e) {
            this.testData.setException(e);
        }

        this.testData.setResultActions(resultActions);
    }

    @When("this user request to deposits to the account:")
    public void thisUserRequestToDepositsToTheAccount(Map<String, String> parameters) {

        String target = parameters.get("accountId");
        String value = parameters.get("value");

        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.put("/accounts/" + target + "/deposit/" + value)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = null;
        try {
            resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
        } catch (Exception e) {
            this.testData.setException(e);
        }

        this.testData.setResultActions(resultActions);
    }

    @When("this user request a withdraw from the account:")
    public void thisUserRequestAWithdrawFromTheAccount(Map<String, String> parameters) {

        String target = parameters.get("accountId");
        String value = parameters.get("value");

        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.put("/accounts/" + target + "/withdraw/" + value)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = null;
        try {
            resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
        } catch (Exception e) {
            this.testData.setException(e);
        }

        this.testData.setResultActions(resultActions);
    }

    @When("this user request transfer from the account:")
    public void thisUserRequestTransferFromTheAccount(Map<String, String> parameters) {

        String source = parameters.get("source");
        String target = parameters.get("target");
        String value = parameters.get("value");

        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.put("/accounts/" + source + "/transfer/"
                        + target + "/" + value)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = null;
        try {
            resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
        } catch (Exception e) {
            this.testData.setException(e);
        }

        this.testData.setResultActions(resultActions);
    }
}
