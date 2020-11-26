package br.com.donus.account.test;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = "br.com.donus.account.test",
        plugin = {"pretty", "html:build/cucumber-html-report"},
        strict = true)
public class AccountApplicationTest {

}