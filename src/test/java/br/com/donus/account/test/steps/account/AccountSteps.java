package br.com.donus.account.test.steps.account;

import br.com.donus.account.data.dto.account.AccountResponse;
import br.com.donus.account.data.entities.Account;
import br.com.donus.account.data.repositories.AccountRepository;
import br.com.donus.account.test.AccountApplicationTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
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
    public void databaseContainsColumnsAs(List<Account> accounts) {
        given(this.accountRepository.findAccountById(any(UUID.class))).willReturn(Optional.empty());
        given(this.accountRepository.findAccountByTaxId(anyString())).willReturn(Optional.empty());
        accounts.forEach(account -> given(this.accountRepository.findAccountById(eq(account.getId()))).willReturn(Optional.of(account)));
        accounts.forEach(account -> given(this.accountRepository.findAccountByTaxId(eq(account.getTaxId()))).willReturn(Optional.of(account)));
    }

    @When("this user requests details about the account {string}")
    public void thisUserRequestsDetailsAboutThisAccount(String accountId) throws Exception {
        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.get("/accounts/id/" + accountId)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);

        this.testData.setResultActions(resultActions);
    }

    @When("this user requests details for all the accounts:")
    public void thisUserRequestsDetailsForAllTheAccounts(Map<String, String> parameters) throws Exception {
        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.get("/accounts/")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON);

        parameters.forEach(mockHttpServletRequestBuilder::queryParam);

        ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);

        this.testData.setResultActions(resultActions);
    }

    @When("this user requests details about the account by the tax id {string}")
    public void thisUserRequestsDetailsAboutThisAccountByTheTaxId(String taxId) throws Exception {
        MockHttpServletRequestBuilder
                mockHttpServletRequestBuilder =
                MockMvcRequestBuilders.get("/accounts/taxid/" + taxId)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);

        this.testData.setResultActions(resultActions);
    }

    @Then("the service will reply this account: {string}")
    public void theServiceWillReplyThisAcount(String json) throws IOException {
        AccountResponse expectedResponse = objectMapper.readValue(json, AccountResponse.class);

        MvcResult mvcResult = this.testData.getResultActions()
                .andReturn();
        AccountResponse response =
                objectMapper.readValue(mvcResult.getResponse()
                        .getContentAsString(), AccountResponse.class);

        Assert.assertEquals(expectedResponse, response);
    }
}
