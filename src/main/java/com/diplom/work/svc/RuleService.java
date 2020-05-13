package com.diplom.work.svc;

import com.diplom.work.core.Client;
import com.diplom.work.core.Rule;
import com.diplom.work.exceptions.ManagerIsNull;
import com.diplom.work.exceptions.TimeIncorrect;
import com.diplom.work.repo.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RuleService {

    private RuleRepository ruleRepository;

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

    public void deleteOneRow(Long id) {
        ruleRepository.deleteById(id);
    }

    public List<Rule> findAllByOrderByIdAsc() {
        return ruleRepository.findAllByOrderByIdAsc();
    }

    public Rule saveWithClients(Rule rule, Set<Client> clientsForEditableRule) throws ManagerIsNull, TimeIncorrect {
        if (rule.getIsForAllClients() == null)
            rule.setIsForAllClients(false);
        if (rule.getIsSmart() == null)
            rule.setIsSmart(false);

        //Если умное правило, затираем инфу о менеджерах, чтобы не мешалась
        if (rule.getIsSmart()) {
            rule.setManager(null);
            rule.setManagerNumber(null);
        } else if (rule.getManager() == null && rule.getManagerNumber() == null)
            throw new ManagerIsNull();

        if (rule.getTimeStart() == null || rule.getTimeFinish() == null || rule.getDays().isEmpty())
            throw new TimeIncorrect();
        //Затираем инфу о конкретных клиентах, если для всех, чтобы не мешалась
        if (rule.getIsForAllClients())
            rule.setClients(new HashSet<>());
        else {
            rule.setClients(clientsForEditableRule);
        }
        return ruleRepository.save(rule);
    }
}
