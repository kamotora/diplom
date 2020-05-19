package com.diplom.work.controller;

import com.diplom.work.core.Log;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.svc.LogService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LogsController {
    private final LogService logService;

    @Autowired
    public LogsController(LogService logService) {
        this.logService = logService;
    }

    /**
     * Вывод страницы с таблицей
     * */
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
        return ResponseEntity.ok(logService.findAllByOrderByTimestampAsc());
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
            exception.printStackTrace(System.err);
            System.err.println(exception.getMessage());
            model.addAttribute("badMessage", "Возникла ошибка при удалении");
        }
        return "fragments/messages :: messages";
    }


    @GetMapping("log/{id}")
    public String deleteLog(@PathVariable Long id) {
        logService.deleteOneLog(id);
        return "redirect:/logs";
    }

}
