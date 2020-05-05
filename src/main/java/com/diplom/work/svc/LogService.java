package com.diplom.work.svc;

import com.diplom.work.core.Log;
import com.diplom.work.repo.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.*;

@Service
public class LogService {
    private LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public Log getOneLogById(Integer id) {
        return logRepository.getOne(id);
    }

    public void saveOneLog(Log log) {
        logRepository.save(log);
    }

    public void updateOneLog(Integer id, String session_id, String type, String state, String from_number, String request_number) {

        Log update = logRepository.getOne(id);
        update.setFrom_number(from_number);
        update.setRequest_number(request_number);
        update.setSession_id(session_id);
        update.setState(state);
        update.setType(type);
        logRepository.save(update);
    }

    public void deleteOneLog(Integer id) {
        logRepository.deleteById(id);
    }

    public List<Log> findAllByOrderByTimestampAsc() {
        List<Log> all = logRepository.findAll();
        all.sort(Comparator.comparing(Log::getTimestampInDateTimeFormat));
        return all;
    }
}
