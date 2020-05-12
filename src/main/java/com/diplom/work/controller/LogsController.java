package com.diplom.work.controller;

import com.diplom.work.core.Log;
import com.diplom.work.core.json.view.LogsViews;
import com.diplom.work.core.json.view.UserViews;
import com.diplom.work.svc.LogService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/logs")
public class LogsController {
    private final LogService logService;

    @Autowired
    public LogsController(LogService logService) {
        this.logService = logService;
    }


    @GetMapping
    public String listLogs(Model model) {
        List<Log> logs = logService.findAllByOrderByTimestampAsc();
        model.addAttribute("logs", logs);
        //model.addAttribute("sort", sortDateMethod);
        return "logs";
    }

    /**
     * Возврат всех логов для таблицы в виде JSON (таблица на JS)
     *
     * @return всех логов в виде JSON
     */
    @GetMapping(path = "/table", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(LogsViews.forTable.class)
    public ResponseEntity<List<Log>> getLogsForTable() {
        return ResponseEntity.ok(logService.findAllByOrderByTimestampAsc());
    }


    /*
    @Secured("Администратор")
    @PostMapping("/saveLogs")
    public String updateLog(@RequestParam String session_id,
                            @RequestParam String type, @RequestParam String state,
                            @RequestParam String from_number, @RequestParam String request_number) {
        workApplicationService.saveOneLog(new Log(session_id,type,state, from_number, request_number));
        return "redirect:/";
    }

     */

    /**
     * Удаление логов по массиву IDs
     * @param ids - массив с ID логов
     */
    @DeleteMapping("log")
    public String deleteLog(@RequestBody List<Long> ids) {
        try {
            ids.forEach(logService::deleteOneLog);
        } catch (UsernameNotFoundException exception) {
            //Хз что ответить)
        }
        return "redirect:/logs";
    }


    @PreAuthorize("hasAuthority('Администратор')")
    @GetMapping("/delete/{id}")
    public String deleteLog(@PathVariable Long id) {
        logService.deleteOneLog(id);
        return "redirect:/logs";
    }

}
