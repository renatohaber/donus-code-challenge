package br.com.donus.account.test.config;

import br.com.donus.account.data.repositories.AccountRepository;
import br.com.donus.account.data.repositories.TransactionRepository;
import br.com.donus.account.test.AccountApplicationTestData;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MockBean(AccountRepository.class)
@MockBean(TransactionRepository.class)
public class AccountApplicationTestConfig {

    @Bean
    public AccountApplicationTestData testData() {
        return new AccountApplicationTestData();
    }

}