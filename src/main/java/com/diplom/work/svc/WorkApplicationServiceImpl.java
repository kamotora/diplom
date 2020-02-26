package com.diplom.work.svc;

import com.diplom.work.core.Log;
import com.diplom.work.core.Rule;
import com.diplom.work.repo.LogRepository;
import com.diplom.work.repo.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkApplicationServiceImpl implements WorkApplicationService {

    @Autowired
    private RuleRepository ruleRepository;
    @Autowired
    private LogRepository logRepository;

    public WorkApplicationServiceImpl(RuleRepository ruleRepository, LogRepository logRepository) {
        this.ruleRepository = ruleRepository;
        this.logRepository = logRepository;
    }

    @Override
    public Rule getOneRowById(Integer id) {
        return ruleRepository.getOne(id);
    }

    @Override
    public void saveOneRow(Rule rule) {
        ruleRepository.save(rule);
    }

    @Override
    public void updateOneRow(Integer id, String client, String number, String FIOClient) {
        Rule update = ruleRepository.getOne(id);
        update.setClientNumber(client);
        update.setClientName(FIOClient);
        update.setManagerNumber(number);
        ruleRepository.save(update);
    }

    @Override
    public void deleteOneRow(Integer id) {
        ruleRepository.deleteById(id);
    }

    @Override
    public List<Rule> findAllByOrderByClientAsc() {
        return ruleRepository.findAllByOrderByClientNameAsc();
    }

    @Override
    public Log getOneLogById(Integer id) {
        return logRepository.getOne(id);
    }

    @Override
    public void saveOneLog(Log log) {
        logRepository.save(log);
    }

    @Override
    public void updateOneLog(Integer id, String session_id, String type, String state, String from_number, String request_number) {
        Log update = logRepository.getOne(id);
        update.setFrom_number(from_number);
        update.setRequest_number(request_number);
        update.setSession_id(session_id);
        update.setState(state);
        update.setType(type);
        logRepository.save(update);
    }

    @Override
    public void deleteOneLog(Integer id) {
        logRepository.deleteById(id);
    }

    @Override
    public List<Log> findAllByOrderByTimestampAsc() {
        return logRepository.findAllByOrderByTimestampInDateTimeFormatAsc();
    }
}
