package com.diplom.work.controller;

import com.diplom.work.core.Log;
import com.diplom.work.svc.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public String listLogs(Model model){
        List<Log> logs = logService.findAllByOrderByTimestampAsc();
        model.addAttribute("logs", logs);
        //model.addAttribute("sort", sortDateMethod);
        return "logs";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editLogs(@PathVariable Integer id, Model model) {
        Log log = logService.getOneLogById(id);
        model.addAttribute("log", log);
        return "operations/logs/editLogs";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/update")
    public String saveLog(@RequestParam Integer id, @RequestParam String session_id,
                          @RequestParam String type, @RequestParam String state,
                          @RequestParam String from_number, @RequestParam String request_number
    ) {
        logService.updateOneLog(id, session_id, type, state, from_number, request_number);
        return "redirect:/logs";
    }


    @PreAuthorize("hasAuthority('ADMIN')")

    @GetMapping("/new")
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
    @GetMapping("/delete/{id}")
    public String deleteLog(@PathVariable Integer id) {
        logService.deleteOneLog(id);
        return "redirect:/logs";
    }

}
