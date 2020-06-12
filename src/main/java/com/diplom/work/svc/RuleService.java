package com.diplom.work.svc;

import com.diplom.work.core.Days;
import com.diplom.work.core.Rule;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.ManagerIsNull;
import com.diplom.work.exceptions.TimeIncorrect;
import com.diplom.work.repo.RuleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    @Autowired
    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public Rule getOneRowById(Long id) {
        return ruleRepository.getOne(id);
    }

    public boolean deleteOneRow(Long id) {
        try {
            Rule rule = ruleRepository.getOne(id);
            rule.getClients().clear();
            ruleRepository.save(rule);
            ruleRepository.delete(rule);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Получить правила, которые действуют для всех клиентов
     *
     * @return список правил или null, если таких нету
     */
    public Set<Rule> getRulesForAll() {
        return ruleRepository.findAllByIsForAllClientsIsTrue();
    }

    /**
     * Может ли правило быть использовано в данный момент по времени и дню
     *
     * @return true - если может, иначе false
     */
    public boolean isRuleCanUseNow(Rule rule) {
        boolean isDayEquals = false;
        LocalDate nowDate = LocalDate.now();
        for (Days day : rule.getDays()) {
            if (day.equals(Days.getByDayOfWeek(DayOfWeek.from(nowDate)))) {
                isDayEquals = true;
                break;
            }
        }

        if (isDayEquals && rule.getTimeStart() != null && rule.getTimeFinish() != null) {
            LocalTime start = rule.getTimeStart().toLocalTime();
            LocalTime finish = rule.getTimeFinish().toLocalTime();
            LocalTime nowTime = LocalTime.now();
            boolean isRuleStartBeforeNow = start.isBefore(nowTime);
            boolean isRuleFinishAfterNow = finish.isAfter(nowTime);

            // Если времена в промежутке одного дня
            // now должно быть в промежутке от start до finish
            if (start.isBefore(finish)) {
                return isRuleStartBeforeNow && isRuleFinishAfterNow;
            }
            // Если времена в переходе с одного дня на другой (20:00 - 10:00)
            // now должно быть в промежутке от start до 24:00 или от 00:00 до finish
            else
                return isRuleStartBeforeNow || isRuleFinishAfterNow;
        } else
            return false;
    }

    public List<Rule> getAll() {
        return ruleRepository.findAll();
    }

    //Список правил для сотрудника
    public List<Rule> getRulesForUser(User user) {
        return ruleRepository.findAllByManager(user);
    }

    //Список правил для сотрудника + умные правила
    public List<Rule> getRulesForUserAndSmartRules(User user) {
        return ruleRepository.findAllByManagerOrIsSmartTrue(user);
    }

    public Rule save(Rule rule) throws TimeIncorrect, ManagerIsNull {
        if (rule.getIsForAllClients() == null)
            rule.setIsForAllClients(false);
        if (rule.getIsSmart() == null)
            rule.setIsSmart(false);

        //Если умное правило, затираем инфу о менеджерах, чтобы не мешалась
        if (Boolean.TRUE.equals(rule.getIsSmart())) {
            rule.setManager(null);
            rule.setManagerNumber(null);
        } else if (rule.getManager() == null && StringUtils.isEmptyOrWhitespace(rule.getManagerNumber()))
            throw new ManagerIsNull();

        if (rule.getTimeStart() == null || rule.getTimeFinish() == null || rule.getDays().isEmpty())
            throw new TimeIncorrect();
        return ruleRepository.save(rule);
    }

    public Rule updateExistingRule(Rule rule) {
        if (rule.getId() == null || rule.getId() == 0)
            return null;
        Rule ruleFromDb = ruleRepository.getOne(rule.getId());
        BeanUtils.copyProperties(rule, ruleFromDb, "id");
        return ruleRepository.save(ruleFromDb);
    }

}
