package com.diplom.work.controller;

import com.diplom.work.core.Rule;
import com.diplom.work.svc.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class RulesController {

    private final RuleService ruleService;
    private String sortDateMethod = "ASC";

    @Autowired
    public RulesController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping("/")
    public String list(Model model){
        List<Rule> rules = filterAndSort();
        model.addAttribute("rules", rules);
        model.addAttribute("sort", sortDateMethod);
        return "index";
    }

    @PreAuthorize("hasAuthority('Администратор')")
    @GetMapping("/new")
    public String newRule() {
        return "operations/new";
    }


    @PreAuthorize("hasAuthority('Администратор')")
    @GetMapping("/edit/{id}")
    public String getEditPage(@PathVariable Integer id, Model model) {
        Rule rule = ruleService.getOneRowById(id);
        model.addAttribute("rule", rule);
        return "operations/edit";
    }

    @PreAuthorize("hasAuthority('Администратор')")
    @PostMapping("/save")
    public String saveRule(Map<String, Object> model, @RequestParam String client, @RequestParam String number, @RequestParam String FIOClient) {
        ruleService.saveOneRow(new Rule(client,number,FIOClient));
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('Администратор')")
    @PostMapping("/update")
    public String saveNote(@RequestParam Integer id, @RequestParam String client,
                           @RequestParam String number, @RequestParam String FIOClient) {

        ruleService.updateOneRow(id, client, number, FIOClient);
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('Администратор')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        ruleService.deleteOneRow(id);
        return "redirect:/";
    }

    @GetMapping("/sort/{sortDate}")
    public String sortChoose(@PathVariable String sortDate) {
        sortDateMethod = sortDate;
        return "redirect:/";
    }

    private List<Rule> filterAndSort() {
        List<Rule> rules = null;
        switch (sortDateMethod) {
            case "ASC":
                rules = ruleService.findAllByOrderByIdAsc();
                break;
        }
        return rules;
    }
}
