package com.diplom.work.svc;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Log;
import com.diplom.work.core.Settings;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
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
    private final SettingsService settingsService = mock(SettingsService.class);
    private final LogService logService = new LogService(logRepository, settingsService);

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
        LocalDate curDate = logFilterDto.getStartDate();
        while (curDate.isBefore(logFilterDto.getFinishDate())) {
            final Log log = new Log();
            log.setTimestampInDateTimeFormat(curDate.atStartOfDay());
            correctLogs.add(log);
            curDate = curDate.plusDays(1);
        }
        curDate = logFilterDto.getFinishDate().plusDays(2);
        List<Log> unCorrectLogs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Log log = new Log();
            log.setTimestampInDateTimeFormat(curDate.atStartOfDay());
            unCorrectLogs.add(log);
            curDate = curDate.plusDays(1);
        }
        List<Log> all = new ArrayList<>(correctLogs);
        all.addAll(unCorrectLogs);
        Mockito.doReturn(all)
                .when(logRepository)
                .findAll();
        final List<Log> allByFilter = logService.findAll(null, logFilterDto);
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
        for (int i = 0; i < MAX_LOGS; i++) {
            final Log log = new Log();
            log.setTimestampInDateTimeFormat(now.plusDays(random.nextInt(MAX_DAY)));
            allLogs.add(log);
            if (random.nextBoolean()) {
                log.setType("outbound");
                log.setFrom_pin(String.valueOf(random.nextInt()));
                log.setRequest_number(SIP_CLIENT_NUMBER);
            } else {
                log.setType("incoming");
                log.setFrom_number(SIP_CLIENT_NUMBER);
                log.setRequest_pin(String.valueOf(random.nextInt()));
            }
        }
        // Создаем самый последний по дате лог с нужным номером менеджера
        final Log correctLog = new Log();
        correctLog.setTimestampInDateTimeFormat(now.plusDays(MAX_DAY + 1));
        if (random.nextBoolean()) {
            correctLog.setType("outbound");
            correctLog.setFrom_pin(CORRECT_MANAGER_NUMBER);
            correctLog.setRequest_number(SIP_CLIENT_NUMBER);
        } else {
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
        assertEquals(lastPinByClientNumber, CORRECT_MANAGER_NUMBER);
    }

    @Test
    public void findAllByOrderByManagerNumber() {
        // Задаем в настройках, что нужно фильтровать по номеру
        Settings settings = new Settings();
        settings.setIsUsersCanViewLogOnlyMyself(true);
        Mockito.doReturn(Optional.of(settings))
                .when(settingsService)
                .getSettingsOptional();

        final String CORRECT_MANAGER_NUMBER = "123";
        final List<Log> correctLogs = new ArrayList<>();
        final int SIZE_CORRECT_LOGS_LIST = 20;
        final Random random = new Random();
        for (int i = 0; i < SIZE_CORRECT_LOGS_LIST; i++) {
            final Log log = new Log();
            log.setTimestampInDateTimeFormat(LocalDateTime.now());
            if (random.nextBoolean()) {
                log.setFrom_pin(CORRECT_MANAGER_NUMBER);
            } else {
                log.setRequest_pin(CORRECT_MANAGER_NUMBER);
            }
            correctLogs.add(log);
        }

        final List<Log> uncorrectLogs = new ArrayList<>();
        final int SIZE_UNCORRECT_LOGS_LIST = 50;

        for (int i = 0; i < SIZE_UNCORRECT_LOGS_LIST; i++) {
            final Log log = new Log();
            log.setTimestampInDateTimeFormat(LocalDateTime.now());
            String randomManagerNumber;
            do {
                randomManagerNumber = String.valueOf(random.nextInt(999));
            } while (randomManagerNumber.equals(CORRECT_MANAGER_NUMBER));
            if (random.nextBoolean()) {
                log.setFrom_pin(randomManagerNumber);
            } else {
                log.setRequest_pin(randomManagerNumber);
            }
            uncorrectLogs.add(log);
        }
        final List<Log> allLogs = new ArrayList<>(correctLogs);
        allLogs.addAll(uncorrectLogs);
        Mockito.doReturn(allLogs)
                .when(logRepository)
                .findAll();
        final User user = new User();
        user.setNumber(CORRECT_MANAGER_NUMBER);
        user.getRoles().add(Role.USER);
        final List<Log> allByManagerNumber = logService.findAll(user, null);

        assertTrue(allByManagerNumber.containsAll(correctLogs) && correctLogs.containsAll(allByManagerNumber));
        allByManagerNumber.retainAll(uncorrectLogs);
        assertTrue(allByManagerNumber.isEmpty());
    }

    @Test
    public void findAllByOrderByTimestampDesc() {
        final List<Log> logs = new ArrayList<>();
        final int SIZE_LOGS_LIST = 20;
        final int RANDOM_BOUND = 50;
        final Random random = new Random();
        for (int i = 0; i < SIZE_LOGS_LIST; i++) {
            final Log log = new Log();
            log.setTimestampInDateTimeFormat(
                    LocalDateTime
                            .now()
                            .plusDays(random.nextInt(RANDOM_BOUND))
                            .plusHours(random.nextInt(RANDOM_BOUND))
                            .plusMinutes(random.nextInt(RANDOM_BOUND))
                            .plusSeconds(random.nextInt(RANDOM_BOUND))
            );
            logs.add(log);
        }
        Mockito.doReturn(logs)
                .when(logRepository)
                .findAll();
        final List<Log> allByOrderByTimestampDesc = logService.findAll(null, null);
        for (int i = 1; i < SIZE_LOGS_LIST; i++)
            assertFalse(allByOrderByTimestampDesc.get(i - 1).getTimestampInDateTimeFormat()
                    .compareTo(allByOrderByTimestampDesc.get(i).getTimestampInDateTimeFormat()) < 0);
    }
}