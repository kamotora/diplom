package com.diplom.work.svc;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Log;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.repo.LogRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class LogServiceTest {
    private final LogRepository logRepository = mock(LogRepository.class);
    private final LogService logService = new LogService(logRepository);

    @Test
    public void deleteOneLog() {
        final Long id = 1L;
        logService.deleteOneLog(id);
        Mockito.verify(logRepository, Mockito.times(1)).deleteById(id);
    }


    @Test
    public void findAllByFilter() {
        List<Log> correctLogs = new ArrayList<>();
        LogFilterDto logFilterDto = new LogFilterDto(LocalDate.now().minusDays(10).toString()
                , LocalDate.now().toString());
        assertNotNull(logFilterDto.getStartDate());
        assertNotNull(logFilterDto.getFinishDate());
        LocalDateTime curDate = logFilterDto.getStartDate();
        while(curDate.isBefore(logFilterDto.getFinishDate())){
            final Log log = new Log();
            log.setTimestampInDateTimeFormat(curDate);
            correctLogs.add(log);
            curDate = curDate.plusDays(1);
        }
        curDate = logFilterDto.getFinishDate().plusDays(2);
        List<Log> unCorrectLogs = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            final Log log = new Log();
            log.setTimestampInDateTimeFormat(curDate);
            unCorrectLogs.add(log);
            curDate = curDate.plusDays(1);
        }
        List<Log> all = new ArrayList<>(correctLogs);
        all.addAll(unCorrectLogs);
        Mockito.doReturn(all)
                .when(logRepository)
                .findAll();
        final List<Log> allByFilter = logService.findAllByFilter(logFilterDto);
        assertTrue(allByFilter.containsAll(correctLogs) && correctLogs.containsAll(allByFilter));
        allByFilter.retainAll(unCorrectLogs);
        assertTrue(allByFilter.isEmpty());
    }

    @Test
    public void findLastPinByClientNumber() throws NumberParseException {
        Set<Log> allLogs = new HashSet<>();
        final LocalDateTime now = LocalDateTime.now();
        final Random random = new Random(now.getNano());
        final String SIP_CLIENT_NUMBER = "sip:14242121@test";
        final String CORRECT_MANAGER_NUMBER = "123";
        final int MAX_DAY = 150;
        final int MAX_LOGS = 10;
        // Создаем MAX_LOGS логов с рандомным номером менеджера
        for (int i = 0; i < MAX_LOGS; i++){
            final Log log = new Log();
            log.setTimestampInDateTimeFormat(now.plusDays(random.nextInt(MAX_DAY)));
            allLogs.add(log);
            if(random.nextBoolean()){
                log.setType("outbound");
                log.setFrom_pin(String.valueOf(random.nextInt()));
                log.setRequest_number(SIP_CLIENT_NUMBER);
            }
            else {
                log.setType("incoming");
                log.setFrom_number(SIP_CLIENT_NUMBER);
                log.setRequest_pin(String.valueOf(random.nextInt()));
            }
        }
        // Создаем самый последний по дате лог с нужным номером менеджера
        final Log correctLog = new Log();
        correctLog.setTimestampInDateTimeFormat(now.plusDays(MAX_DAY+1));
        if(random.nextBoolean()){
            correctLog.setType("outbound");
            correctLog.setFrom_pin(CORRECT_MANAGER_NUMBER);
            correctLog.setRequest_number(SIP_CLIENT_NUMBER);
        }
        else {
            correctLog.setType("incoming");
            correctLog.setFrom_number(SIP_CLIENT_NUMBER);
            correctLog.setRequest_pin(CORRECT_MANAGER_NUMBER);
        }
        allLogs.add(correctLog);
        Mockito.doReturn(allLogs)
                .when(logRepository)
                .findAllByState("connected");
        final String lastPinByClientNumber = logService.
                findLastPinByClientNumber(ControllerUtils.parseNumberFromSip(SIP_CLIENT_NUMBER));
        assertEquals(lastPinByClientNumber,CORRECT_MANAGER_NUMBER);
    }
}