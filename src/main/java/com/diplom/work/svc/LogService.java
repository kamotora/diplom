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

    public void deleteOneLog(Long id) {
        logRepository.deleteById(id);
    }

    public List<Log> findAllByOrderByTimestampAsc() {
        List<Log> all = logRepository.findAll();
        all.sort(Comparator.comparing(Log::getTimestampInDateTimeFormat));
        return all;
    }
}
