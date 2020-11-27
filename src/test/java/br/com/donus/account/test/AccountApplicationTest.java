package br.com.donus.account.test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = "br.com.donus.account.test",
        plugin = {"pretty", "html:build/cucumber-html-report"},
        strict = true)
public class AccountApplicationTest {

}