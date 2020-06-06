package com.diplom.work.svc;

import com.diplom.work.core.Log;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.repo.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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

    /**
     * Поиск всех логов по фильтру
     * если logFilterDto.getStartDate() или logFilterDto.getFinishDate() null, то они не учитываются
     */
    public List<Log> findAllByFilter(LogFilterDto logFilterDto) {
        return logRepository.findAll().stream().filter(log ->
                {
                    if (log == null || log.getTimestampInDateTimeFormat() == null)
                        return false;
                    LocalDate logDate = log.getTimestampInDateTimeFormat().toLocalDate();
                    LocalDate startDate = logFilterDto.getStartDate() != null
                            ? logFilterDto.getStartDate().toLocalDate() : null;
                    LocalDate finishDate = logFilterDto.getFinishDate() != null
                            ? logFilterDto.getFinishDate().toLocalDate() : null;
                    boolean logDateAfterOrEqualStart =
                            startDate == null || logDate.isAfter(startDate) || logDate.isEqual(startDate);
                    boolean logDateBeforeOrEqualFinish =
                            finishDate == null || logDate.isBefore(finishDate) || logDate.isEqual(finishDate);
                    return logDateAfterOrEqualStart && logDateBeforeOrEqualFinish;
                }
        )
                .collect(Collectors.toList());
    }

    /**
     * Ищем последний pin, с которого был разговор с клиентом clientNumber
     *
     * @param clientNumber номер клиента
     * @return pin или null, если ничего не нашли
     */
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
            } else if (clientNumber.equals(parseNumberFromSip(log.getRequest_number())) && !log.getFrom_pin().isEmpty()) {
                return log.getFrom_pin();
            }

        }
        return null;
    }
}
