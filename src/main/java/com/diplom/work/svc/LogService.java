package com.diplom.work.svc;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Log;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.repo.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.diplom.work.controller.ControllerUtils.parseNumberFromSip;
import static org.thymeleaf.util.StringUtils.isEmptyOrWhitespace;

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
        return logRepository.findAll().stream().filter(log ->
                log.getTimestampInDateTimeFormat().isAfter(logFilterDto.getStartDate())
                        && log.getTimestampInDateTimeFormat().isBefore(logFilterDto.getFinishDate().plusDays(1)))
                .collect(Collectors.toList());
    }

    /**
     * @param clientNumber номер клиента
     * Ищем последний pin, с которого был разговор с клиентом clientNumber
     * @return pin или null, если ничего не нашли
     * */
    public String findLastPinByClientNumber(String clientNumber) throws NumberParseException {
        if (isEmptyOrWhitespace(clientNumber))
            return null;
        List<Log> connected = logRepository.findAllByState("connected").stream()
                .sorted(Comparator.comparing(Log::getTimestampInDateTimeFormat).reversed()).collect(Collectors.toList());
        for (Log log : connected) {
            if (log.isInternal())
                continue;
            // Если исходящий, номер звонящего должен совпадать с clientNumber
            // Если указан pin, вернём его
            if (log.isIncoming()) {
                if (clientNumber.equals(parseNumberFromSip(log.getFrom_number())) && !log.getRequest_pin().isEmpty()) {
                    return log.getRequest_pin();
                }
            }else if(clientNumber.equals(parseNumberFromSip(log.getRequest_number())) && !log.getFrom_pin().isEmpty()){
                return log.getFrom_pin();
            }

        }
        return null;
    }
}
