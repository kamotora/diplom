package com.diplom.work;

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

    @PostMapping("/update")
    public String saveNote(@RequestParam Integer id, @RequestParam String client,
                           @RequestParam Integer number, @RequestParam String FIOClient) {
        workApplicationService.updateOneRow(id, client, number, FIOClient);
        return "redirect:/";
    }

    @GetMapping("/new")
    public String newNote() {
        return "operations/new";
    }

    @PostMapping("/save")
    public String updateNote(@RequestParam String client,@RequestParam Integer number, @RequestParam String FIOClient) {
        workApplicationService.saveOneRow(new OneRow(client,number,FIOClient));
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        workApplicationService.deleteOneRow(id);
        return "redirect:/";
    }

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
