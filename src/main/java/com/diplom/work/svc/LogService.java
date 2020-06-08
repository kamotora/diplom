package com.diplom.work.svc;

import com.diplom.work.core.Log;
import com.diplom.work.core.Settings;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.repo.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.diplom.work.controller.ControllerUtils.parseNumberFromSip;
import static org.thymeleaf.util.StringUtils.isEmptyOrWhitespace;

@Service
public class LogService {
    private final LogRepository logRepository;
    private final SettingsService settingsService;

    @Autowired
    public LogService(LogRepository logRepository, SettingsService settingsService) {
        this.logRepository = logRepository;
        this.settingsService = settingsService;
    }

    public void deleteOneLog(Long id) {
        logRepository.deleteById(id);
    }

    /**
     * Вернуть все логи.
     * Указать <code>user</code>, если нужно отобразить звонки, где менеджер участвует в качестве вызывающего или вызываемого абонента
     * При этом будет проверка, если у user роль - пользователь и стоит ли галка в настройках
     * Иначе фильтр не сработает
     * Указать <code>logFilterDto</code>, если нужно отфильтровать звонки по дате.
     * Eсли logFilterDto.getStartDate() или logFilterDto.getFinishDate() null, то они не учитываются
     *
     * @param user         текущий пользователь
     * @param logFilterDto дата начала и окончания для фильтра
     * @return список логов
     */

    public List<Log> findAll(@Nullable User user, @Nullable LogFilterDto logFilterDto) {
        final List<Log> all = logRepository.findAll();
        Stream<Log> stream = all.stream();
        // Добавляем сортировку по дате (сначала - новые)
        stream = stream.sorted(Comparator.comparing(Log::getTimestampInDateTimeFormat).reversed());
        final Optional<Settings> settingsOptional = settingsService.getSettingsOptional();

        // Добавляем фильтр по номеру менеджера
        if (
                settingsOptional.isPresent()
                        && Boolean.TRUE.equals(settingsOptional.get().getIsUsersCanViewLogOnlyMyself()
                        && user != null
                        && user.getRoles().contains(Role.USER)
                        && !isEmptyOrWhitespace(user.getNumber()))
        ) {
            stream = stream.filter(log -> user.getNumber().equals(log.getRequest_pin()) || user.getNumber().equals(log.getFrom_pin()));
        }
        // Добавляем фильтр по датам
        if (logFilterDto != null) {
            stream = stream.filter(log -> {
                if (log.getTimestampInDateTimeFormat() == null)
                    return false;
                LocalDate logDate = log.getTimestampInDateTimeFormat().toLocalDate();
                return (logFilterDto.getStartDate() == null || logFilterDto.getStartDate().compareTo(logDate) < 1) &&
                        (logFilterDto.getFinishDate() == null || logFilterDto.getFinishDate().compareTo(logDate) > -1);

            });
        }
        return stream.collect(Collectors.toList());
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
