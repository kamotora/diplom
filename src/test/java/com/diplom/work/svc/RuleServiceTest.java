package com.diplom.work.svc;

import com.diplom.work.core.Days;
import com.diplom.work.core.Rule;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.ManagerIsNull;
import com.diplom.work.exceptions.TimeIncorrect;
import com.diplom.work.repo.RuleRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RuleServiceTest {
    private final RuleRepository ruleRepository = mock(RuleRepository.class);
    private final RuleService ruleService = new RuleService(ruleRepository);

    @Test
    public void isRuleCanUseNow() {
        Rule correctRule = new Rule();
        correctRule.getDays().add(Days.getByDayOfWeek(LocalDate.now().getDayOfWeek()));
        correctRule.setTimeStart(Time.valueOf(LocalTime.now().minusMinutes(1)));
        correctRule.setTimeFinish(Time.valueOf(LocalTime.now().plusMinutes(1)));
        assertTrue(ruleService.isRuleCanUseNow(correctRule));

        Rule incorrectDay = new Rule();
        incorrectDay.getDays().add(Days.getByDayOfWeek(LocalDate.now().plusDays(1).getDayOfWeek()));
        incorrectDay.setTimeStart(Time.valueOf(LocalTime.now().minusMinutes(1)));
        incorrectDay.setTimeFinish(Time.valueOf(LocalTime.now().plusMinutes(1)));
        assertFalse(ruleService.isRuleCanUseNow(incorrectDay));

        Rule incorrectTime = new Rule();
        incorrectTime.getDays().add(Days.getByDayOfWeek(LocalDate.now().getDayOfWeek()));
        incorrectTime.setTimeStart(Time.valueOf(LocalTime.now().minusMinutes(2)));
        incorrectTime.setTimeFinish(Time.valueOf(LocalTime.now().minusMinutes(1)));
        assertFalse(ruleService.isRuleCanUseNow(incorrectTime));
    }

    @Test
    public void save() throws TimeIncorrect, ManagerIsNull {
        Rule incorrectRule = new Rule();
        incorrectRule.getDays().add(Days.getByDayOfWeek(LocalDate.now().getDayOfWeek()));
        assertThrows(ManagerIsNull.class, () -> ruleService.save(incorrectRule));
        incorrectRule.setManager(new User());
        incorrectRule.setTimeStart(Time.valueOf(LocalTime.now()));
        assertThrows(TimeIncorrect.class, () -> ruleService.save(incorrectRule));
        incorrectRule.setTimeFinish(Time.valueOf(LocalTime.now().minusHours(1)));
        assertNull(ruleService.save(incorrectRule));
        Mockito.verify(ruleRepository, Mockito.times(1)).save(incorrectRule);
    }
}