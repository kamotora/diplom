package com.diplom.work.controller;

import com.diplom.work.core.Log;
import com.diplom.work.core.Rule;
import com.diplom.work.svc.WorkApplicationService;
import com.diplom.work.svc.WorkApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WorkApplicationController {

    //TODO Разделить логи и главную на 2 контроллера

    private WorkApplicationService workApplicationService;
    private String sortDateMethod = "ASC";

    @Autowired
    public void setWorkApplicationService(WorkApplicationServiceImpl workApplicationService){
        this.workApplicationService = workApplicationService;
    }

    @PostMapping("/home")
    public String homePageAfterLogin(Model model){
        return "redirect:/";
    }

    @GetMapping("/")
    public String list(Model model){
        List<Rule> rules = filterAndSort();
        model.addAttribute("rules", rules);
        model.addAttribute("sort", sortDateMethod);
        return "index";
    }


    @GetMapping("/logs")
    public String listLogs(Model model){
        List<Log> logs = workApplicationService.findAllByOrderByTimestampAsc();
        model.addAttribute("logs", logs);
        model.addAttribute("sort", sortDateMethod);
        return "logs";
    }

    @GetMapping("/sort/{sortDate}")
    public String sortChoose(@PathVariable String sortDate) {
        sortDateMethod = sortDate;
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Rule rule = workApplicationService.getOneRowById(id);
        model.addAttribute("rule", rule);
        return "operations/edit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/editLogs/{id}")
    public String editLogs(@PathVariable Integer id, Model model) {
        Log log = workApplicationService.getOneLogById(id);
        model.addAttribute("log", log);
        return "operations/logs/editLogs";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/update")
    public String saveNote(@RequestParam Integer id, @RequestParam String client,
                           @RequestParam String number, @RequestParam String FIOClient) {
        workApplicationService.updateOneRow(id, client, number, FIOClient);
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/updateLogs")
    public String saveLog(@RequestParam Integer id, @RequestParam String session,
                           @RequestParam String type, @RequestParam String state,
                            @RequestParam String from_number, @RequestParam String request_number
                          ) {
        workApplicationService.updateOneLog(id, session, type, state, from_number, request_number);
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/new")
    public String newNote() {
        return "operations/new";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/logs/newLogs")
    public String newLog() {
        return "operations/logs/newLogs";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/save")
    public String updateNote(@RequestParam String client,@RequestParam String number, @RequestParam String FIOClient) {
        workApplicationService.saveOneRow(new Rule(client,number,FIOClient));
        return "redirect:/";
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
    public String delete(@PathVariable Integer id) {
        workApplicationService.deleteOneRow(id);
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/deleteLogs/{id}")
    public String deleteLog(@PathVariable Integer id) {
        workApplicationService.deleteOneLog(id);
        return "redirect:/";
    }

    //Ветку потестить
    private List<Rule> filterAndSort() {
        List<Rule> rules = null;
        switch (sortDateMethod) {
            case "ASC":
                rules = workApplicationService.findAllByOrderByClientAsc();
                break;
        }
        return rules;
    }

}
