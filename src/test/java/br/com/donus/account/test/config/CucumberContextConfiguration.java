package br.com.donus.account.test.config;

import br.com.donus.account.AccountApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {AccountApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberContextConfiguration {
}
