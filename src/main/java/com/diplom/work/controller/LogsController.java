package com.diplom.work.controller;

import com.diplom.work.core.Log;
import com.diplom.work.svc.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class LogsController {
    private final LogService logService;

    @Autowired
    public LogsController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs")
    public String listLogs(Model model){
        List<Log> logs = logService.findAllByOrderByTimestampAsc();
        model.addAttribute("logs", logs);
        //model.addAttribute("sort", sortDateMethod);
        return "logs";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/editLogs/{id}")
    public String editLogs(@PathVariable Integer id, Model model) {
        Log log = logService.getOneLogById(id);
        model.addAttribute("log", log);
        return "operations/logs/editLogs";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/updateLogs")
    public String saveLog(@RequestParam Integer id, @RequestParam String session,
                          @RequestParam String type, @RequestParam String state,
                          @RequestParam String from_number, @RequestParam String request_number
    ) {
        logService.updateOneLog(id, session, type, state, from_number, request_number);
        return "redirect:/";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/logs/newLogs")
    public String newLog() {
        return "operations/logs/newLogs";
    }


    /*
    @Secured("ADMIN")
    @PostMapping("/saveLogs")
    public String updateLog(@RequestParam String session_id,
                            @RequestParam String type, @RequestParam String state,
                            @RequestParam String from_number, @RequestParam String request_number) {
        workApplicationService.saveOneLog(new Log(session_id,type,state, from_number, request_number));
        return "redirect:/";
    }

     */

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/deleteLogs/{id}")
    public String deleteLog(@PathVariable Integer id) {
        logService.deleteOneLog(id);
        return "redirect:/";
    }

}
