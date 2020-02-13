package com.diplom.work;

import com.diplom.work.core.OneLog;
import com.diplom.work.core.OneRow;
import com.diplom.work.svc.WorkApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WorkApplicationController {

    private WorkApplicationService workApplicationService;
    private String sortDateMethod = "ASC";

    @Autowired
    public void setWorkApplicationService(WorkApplicationService workApplicationService){
        this.workApplicationService = workApplicationService;
    }

    @GetMapping("/")
    public String list(Model model){
        List<OneRow> oneRows = filterAndSort();
        model.addAttribute("oneRows", oneRows);
        model.addAttribute("sort", sortDateMethod);
        return "index";
    }

    @GetMapping("/logs")
    public String listLogs(Model model){
        List<OneLog> oneLogs = workApplicationService.findAllByOrderBySessionAsc();
        model.addAttribute("oneLogs", oneLogs);
        model.addAttribute("sort", sortDateMethod);
        return "logs";
    }

    @GetMapping("/sort/{sortDate}")
    public String sortChoose(@PathVariable String sortDate) {
        sortDateMethod = sortDate;
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        OneRow oneRow = workApplicationService.getOneRowById(id);
        model.addAttribute("oneRow", oneRow);
        return "operations/edit";
    }

    @GetMapping("/editLogs/{id}")
    public String editLogs(@PathVariable Integer id, Model model) {
        OneLog oneLog = workApplicationService.getOneLogById(id);
        model.addAttribute("oneLog", oneLog);
        return "operations/logs/editLogs";
    }

    @PostMapping("/update")
    public String saveNote(@RequestParam Integer id, @RequestParam String client,
                           @RequestParam String number, @RequestParam String FIOClient) {
        workApplicationService.updateOneRow(id, client, number, FIOClient);
        return "redirect:/";
    }

    @PostMapping("/updateLogs")
    public String saveLog(@RequestParam Integer id, @RequestParam String session,
                           @RequestParam String type, @RequestParam String state,
                            @RequestParam String from_number, @RequestParam String request_number
                          ) {
        workApplicationService.updateOneLog(id, session, type, state, from_number, request_number);
        return "redirect:/";
    }

    @GetMapping("/new")
    public String newNote() {
        return "operations/new";
    }

    @GetMapping("/logs/newLogs")
    public String newLog() {
        return "operations/logs/newLogs";
    }


    @PostMapping("/save")
    public String updateNote(@RequestParam String client,@RequestParam String number, @RequestParam String FIOClient) {
        workApplicationService.saveOneRow(new OneRow(client,number,FIOClient));
        return "redirect:/";
    }

    @PostMapping("/saveLogs")
    public String updateLog(@RequestParam String session,
                            @RequestParam String type, @RequestParam String state,
                            @RequestParam String from_number, @RequestParam String request_number) {
        workApplicationService.saveOneLog(new OneLog(session,type,state, from_number, request_number));
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        workApplicationService.deleteOneRow(id);
        return "redirect:/";
    }

    @GetMapping("/deleteLogs/{id}")
    public String deleteLog(@PathVariable Integer id) {
        workApplicationService.deleteOneLog(id);
        return "redirect:/";
    }

    //Ветку потестить
    private List<OneRow> filterAndSort() {
        List<OneRow> oneRows = null;
        switch (sortDateMethod) {
            case "ASC":
                oneRows = workApplicationService.findAllByOrderByClientAsc();
                break;
        }
        return oneRows;
    }

}
