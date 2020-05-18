package com.diplom.work.svc;

import com.diplom.work.core.Client;
import com.diplom.work.core.Days;
import com.diplom.work.core.Rule;
import com.diplom.work.exceptions.ManagerIsNull;
import com.diplom.work.exceptions.TimeIncorrect;
import com.diplom.work.repo.RuleRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashSet;
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

    public void saveOneRow(Rule rule) {
        ruleRepository.save(rule);
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

    public List<Rule> findAllByOrderByIdAsc() {
        return ruleRepository.findAllByOrderByIdAsc();
    }

    /**
     * Получить правила, которые действуют для всех клиентов
     *
     * @return список правил или null, если таких нету
     */
    public Set<Rule> getRulesForAll() {
        Set<Rule> all = ruleRepository.findAllByIsForAllClientsIsTrue();
        return all;
    }

    public Rule saveWithClients(Rule rule, Set<Client> clientsForEditableRule) throws ManagerIsNull, TimeIncorrect {
        //Затираем инфу о конкретных клиентах, если для всех, чтобы не мешалась
        rule = save(rule);
        if (rule.getIsForAllClients())
            rule.setClients(new HashSet<>());
        else {
            rule.setClients(clientsForEditableRule);
        }
        return ruleRepository.save(rule);
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

        if (isDayEquals) {
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

    /**
     * Получить первое правило для всех клиентов, которое дейсвтвует в данный момент
     *
     * @return правило, подходящее под условие или null, если не найдено
     */
    public Rule getFirstRuleForAllCanUseNow() {
        return getRulesForAll().stream().filter(this::isRuleCanUseNow).findFirst().orElse(null);
    }

    public List<Rule> getAll() {
        return ruleRepository.findAll();
    }

    public Rule save(Rule rule) throws TimeIncorrect, ManagerIsNull {
        if (rule.getIsForAllClients() == null)
            rule.setIsForAllClients(false);
        if (rule.getIsSmart() == null)
            rule.setIsSmart(false);

        //Если умное правило, затираем инфу о менеджерах, чтобы не мешалась
        if (rule.getIsSmart()) {
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

    public void removeClientFromRule(Rule rule, Client client) {
        rule.getClients().remove(client);
        ruleRepository.save(rule);
    }
}
