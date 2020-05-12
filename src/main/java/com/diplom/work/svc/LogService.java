package com.diplom.work.svc;

import com.diplom.work.core.Log;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.repo.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogService {
    private final LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void deleteOneLog(Long id) {
        logRepository.deleteById(id);
    }

    public List<Log> findAllByOrderByTimestampAsc() {
        List<Log> all = logRepository.findAll();
        all.sort(Comparator.comparing(Log::getTimestampInDateTimeFormat).reversed());
        return all;
    }

    public List<Log> findAllByFilter(LogFilterDto logFilterDto) {
        System.out.println("kek");
        return logRepository.findAll().stream().filter(log ->
                log.getTimestampInDateTimeFormat().isAfter(logFilterDto.getStartDate())
                        && log.getTimestampInDateTimeFormat().isBefore(logFilterDto.getFinishDate().plusDays(1)))
                .collect(Collectors.toList());
    }
}
