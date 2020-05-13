package com.diplom.work.controller;

import com.diplom.work.core.Rule;
import com.diplom.work.core.json.view.LogsViews;
import com.diplom.work.core.user.User;
import com.diplom.work.svc.RuleService;
import com.diplom.work.svc.UserService;
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
import java.util.Map;

@Controller
public class RulesController {

    private final RuleService ruleService;
    private final UserService userService;
    private String sortDateMethod = "ASC";

    @Autowired
    public RulesController(RuleService ruleService, UserService userService) {
        this.ruleService = ruleService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String list(Model model){
        List<Rule> rules = filterAndSort();
        model.addAttribute("rules", rules);
        model.addAttribute("sort", sortDateMethod);
        return "index";
    }


    /**
     * Возврат всех правил для таблицы в виде JSON (таблица на JS)
     *
     * @return всех правил в виде JSON
     */
    @GetMapping(path = "api/ruleTable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(LogsViews.forTable.class)
    public ResponseEntity<List<Rule>> getRulesForTable() {
        return ResponseEntity.ok(ruleService.findAllByOrderByIdAsc());
    }

    @PreAuthorize("hasAuthority('Администратор')")
    @GetMapping("/new")
    public String newRule(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "operations/new";
    }


    @PreAuthorize("hasAuthority('Администратор')")
    @GetMapping("/edit/{id}")
    public String getEditPage(@PathVariable Long id, Model model) {
        Rule rule = ruleService.getOneRowById(id);
        model.addAttribute("rule", rule);
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "operations/edit";
    }


    @GetMapping("/view/{id}")
    public String getViewPage(@PathVariable Long id, Model model) {
        Rule rule = ruleService.getOneRowById(id);
        model.addAttribute("rule", rule);
        return "operations/view";
    }

    @PreAuthorize("hasAuthority('Администратор')")
    @PostMapping("/save")
    public String saveRule(Map<String, Object> model, @RequestParam String client, @RequestParam String number, @RequestParam String FIOClient, @RequestParam String name) {
        ruleService.saveOneRow(new Rule(client,number,FIOClient, name));
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('Администратор')")
    @PostMapping("/update")
    public String saveNote(@RequestParam Long id, @RequestParam String client,
                           @RequestParam String number, @RequestParam String FIOClient, @RequestParam String NameRout) {

        ruleService.updateOneRow(id, client, number, FIOClient, NameRout);
        return "redirect:/";
    }

    /**
     * Удаление правил по массиву IDs
     * @param ids - массив с ID правила
     */
    @DeleteMapping("rule")
    public String deleteRule(@RequestBody List<Long> ids) {
        try {
            ids.forEach(ruleService::deleteOneRow);
        } catch (UsernameNotFoundException exception) {
            exception.printStackTrace(System.err);
            System.err.println(exception.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("rule/{id}")
    public String deleteRule(@PathVariable Long id) {
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
