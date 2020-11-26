package br.com.donus.account.test;

import br.com.donus.account.test.steps.common.EntitySampleFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class AccountApplicationTestData {

    private String requestJson;
    private String logon;
    private ResultActions resultActions;
    private LocalDateTime now;

    public AccountApplicationTestData() {
        this.reset();
    }

    public void reset() {
        this.requestJson = null;
        this.logon = null;
        this.resultActions = null;
        this.now = null;
    }

    public LocalDateTime getFirstDayOfMonth() {
        return EntitySampleFactory.getFirstDayOfCurrentMonth(now);
    }

}
