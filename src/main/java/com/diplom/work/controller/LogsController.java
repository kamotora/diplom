package com.diplom.work.controller;

import com.diplom.work.core.Log;
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
    @GetMapping(path = "/logs", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.forTable.class)
    public ResponseEntity<List<Log>> getLogsForTable() {
        List<Log> ok = logService.findAllByOrderByTimestampAsc();
        return ResponseEntity.ok(ok);
    }

    /**
     * Возврат логов для таблицы в виде JSON (таблица на JS) ПО ФИЛЬТРУ
     *
     * @return всех логов в виде JSON
     */
    @PostMapping(path = "/logs", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.forTable.class)
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

    //TECT

    /**
     * Получаем от ВАТС инфу о вызове
     * и выводим её
     * но пока нихуя не работает ибо метод отлключен и хз как отлаживать
     */
    @GetMapping("log/{id}/view")
    public ResponseEntity<String> showCallInfo(@PathVariable("id") Log log) {
        try {
            return ResponseEntity.ok(callService.getCallInfoBySessionID(log.getSession_id()).toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

}
