package br.com.donus.account.test.steps.common;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class EntitySampleFactory {

    public static LocalDateTime getFirstDayOfCurrentMonth(LocalDateTime currentDate) {
        if (Objects.isNull(currentDate)) {
            return null;
        }
        return currentDate.withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);
    }

}