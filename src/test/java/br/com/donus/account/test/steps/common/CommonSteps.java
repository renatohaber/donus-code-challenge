package br.com.donus.account.test.steps.common;

import br.com.donus.account.AccountApplication;
import br.com.donus.account.data.entities.Account;
import br.com.donus.account.data.entities.Transaction;
import br.com.donus.account.data.repositories.AccountRepository;
import br.com.donus.account.data.repositories.TransactionRepository;
import br.com.donus.account.test.AccountApplicationTestData;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = {AccountApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommonSteps {

    private final AccountApplicationTestData testData;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public CommonSteps(AccountApplicationTestData testData, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.testData = testData;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Before
    public void setUp() {
        this.testData.reset();
    }

    @After
    public void wrapUp() {
        reset(accountRepository);
        reset(transactionRepository);
    }

    @Given("a user as follows:")
    public void aUser(Map<String, String> userInformation) {
        this.testData.setLogon(userInformation.get("logon"));
    }

    @Given("the database is offline")
    public void databaseIsOffline() {
        this.setAccountRepositoryOffline();
        this.setTransactionRepositoryOffline();
    }

    @Then("the system will return {string} status code")
    public void theSystemWillReturnStatusCodeString(String statusRaw) throws Exception {
        theSystemWillReturnStatusCode(Integer.parseInt(statusRaw));
    }

    @Then("the system will return {int} status code")
    public void theSystemWillReturnStatusCode(int status) throws Exception {
        if (testData.getException() != null) {
            this.testData.getResultActions()
                    .andExpect(status().is(status));

            if (status == HttpStatus.NO_CONTENT.value()) {
                Assert.assertTrue(StringUtils.isBlank(this.testData.getResultActions()
                        .andReturn()
                        .getResponse()
                        .getContentAsString()));
            }
        }
    }

    private void setAccountRepositoryOffline() {
        given(this.accountRepository.save(any(Account.class)))
                .willThrow(RuntimeException.class);

        willThrow(RuntimeException.class)
                .given(this.accountRepository)
                .deleteById(any(UUID.class));

        given(this.accountRepository.findAccountById(any(UUID.class)))
                .willThrow(RuntimeException.class);

        given(this.accountRepository.findAccountByTaxId(anyString()))
                .willThrow(RuntimeException.class);

        given(this.accountRepository.findAll(any(Pageable.class)))
                .willThrow(RuntimeException.class);
    }

    private void setTransactionRepositoryOffline() {
        given(this.transactionRepository.save(any(Transaction.class)))
                .willThrow(RuntimeException.class);

        willThrow(RuntimeException.class)
                .given(this.transactionRepository)
                .deleteById(any(UUID.class));

        given(this.transactionRepository.findByAccountIdAndCreationDateBetweenOrderByCreationDateDesc(any(UUID.class), any(), any()))
                .willThrow(RuntimeException.class);
    }

    @Then("an exception is thrown")
    public void anExceptionIsThrown() {
        Assert.assertNotNull(this.testData.getException());
    }
}