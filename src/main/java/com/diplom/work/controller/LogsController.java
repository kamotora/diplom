package com.diplom.work.controller;

import com.diplom.work.core.Log;
import com.diplom.work.core.dto.CallInfo;
import com.diplom.work.core.dto.GetRecord;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.svc.CallService;
import com.diplom.work.svc.LogService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@Slf4j
public class LogsController {
    private final LogService logService;
    private final CallService callService;

    @Autowired
    public LogsController(LogService logService, CallService callService) {
        this.logService = logService;
        this.callService = callService;
    }

    /**
     * Вывод страницы с таблицей
     */
    @GetMapping(path = "/logs")
    public String listLogs(Model model) {
        return "logs";
    }

    /**
     * Возврат всех логов для таблицы в виде JSON (таблица на JS)
     *
     * @return всех логов в виде JSON
     */
    @GetMapping(path = "/logs/table", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.ForTable.class)
    public ResponseEntity<List<Log>> getLogsForTable() {
        List<Log> ok = logService.findAllByOrderByTimestampAsc();
        return ResponseEntity.ok(ok);
    }

    /**
     * Возврат логов для таблицы в виде JSON (таблица на JS) ПО ФИЛЬТРУ
     *
     * @return всех логов в виде JSON
     */
    @PostMapping(path = "/logs/table", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.ForTable.class)
    public ResponseEntity<List<Log>> getLogsForTableByFilter(@RequestBody LogFilterDto logFilterDto) {
        List<Log> logs = logService.findAllByFilter(logFilterDto);
        return ResponseEntity.ok(logs);
    }

    /**
     * Удаление логов по массиву IDs
     *
     * @param ids - массив с ID логов
     * @return блок с сообщениями об успехе/ошибке для его вывода через jquery
     */
    @DeleteMapping("log")
    public String deleteLog(Model model, @RequestBody List<Long> ids) {
        try {
            ids.forEach(logService::deleteOneLog);
            model.addAttribute("goodMessage", "Удалено");
        } catch (Exception exception) {
            log.error("Ошибка при удалении лога: {}", exception.getMessage());
            model.addAttribute("badMessage", "Возникла ошибка при удалении");
        }
        return "fragments/messages :: messages";
    }

    /**
     * Получаем от ВАТС инфу о вызове
     * и выводим её
     */
    @GetMapping("/log/{id}/view")
    public String showCallInfo(@PathVariable("id") Log log, Model model, HttpServletRequest request) {
        try {
            String ipClient = request.getHeader("X-Forwarded-For");
            CallInfo callInfo = callService.getCallInfoBySessionID(log.getSession_id());
            GetRecord record = callService.getRecordBySessionID(log.getSession_id(), ipClient);
            if (!record.getResult().equals("0")) {
                record = null;
            }
            if (callInfo.getInfo() == null) {
                callInfo = null;
                model.addAttribute("messageError", "Не удалось получить информацию по вызову");
            }
            model.addAttribute("nameSession", log.getSession_id());
            model.addAttribute("callInfo", callInfo);
            model.addAttribute("record", record);
            return "log";
        } catch (Exception e) {

            model.addAttribute("messageError", "Не удалось получить информацию по вызову");
            model.addAttribute("nameSession", log.getSession_id());
            return "log";
        }
    }

    /**
     * Получаем от ВАТС запись звонка
     * но пока нихуя не работает ибо метод отлключен и хз как отлаживать
     */
    @GetMapping("log/{id}/record")
    public ResponseEntity<GetRecord> showCallRecord(@PathVariable("id") Log log, Model model, HttpServletRequest request) {
        GetRecord errorRecord = new GetRecord("-1000", "Не удалось получить запись", "");
        String ipClient = request.getHeader("X-Forwarded-For");
        try {
            GetRecord record = callService.getRecordBySessionID(log.getSession_id(), ipClient);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(errorRecord);
        }
    }

}
