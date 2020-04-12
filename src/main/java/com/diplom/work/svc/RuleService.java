package com.diplom.work.svc;

import com.diplom.work.core.Rule;
import com.diplom.work.repo.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {

    private RuleRepository ruleRepository;
    @Autowired
    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public Rule getOneRowById(Integer id) {
        return ruleRepository.getOne(id);
    }

    public void saveOneRow(Rule rule) {
        ruleRepository.save(rule);
    }

    public void updateOneRow(Integer id, String client, String number, String FIOClient) {
        Rule update = ruleRepository.getOne(id);
        update.setClientNumber(client);
        update.setClientName(FIOClient);
        update.setManagerNumber(number);
        ruleRepository.save(update);
    }
    public void deleteOneRow(Integer id) {
        ruleRepository.deleteById(id);
    }

    public List<Rule> findAllByOrderByIdAsc() {
        return ruleRepository.findAllByOrderByIdAsc();
    }

}
