package com.diplom.work.controller;

import com.diplom.work.core.Client;
import com.diplom.work.core.Days;
import com.diplom.work.core.Rule;
import com.diplom.work.core.json.view.RuleViews;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.ManagerIsNull;
import com.diplom.work.exceptions.TimeIncorrect;
import com.diplom.work.svc.RuleService;
import com.diplom.work.svc.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class RulesController {

    private final RuleService ruleService;
    private final UserService userService;
    private String sortDateMethod = "ASC";

    //todo поменять потом
    private Set<Client> clientsForEditableRule = new HashSet<>();

    @Autowired
    public RulesController(RuleService ruleService, UserService userService) {
        this.ruleService = ruleService;
        this.userService = userService;
    }

    @GetMapping("/rules")
    public String list(Model model) {
        List<Rule> rules = filterAndSort();
        model.addAttribute("rules", rules);
        model.addAttribute("sort", sortDateMethod);
        return "rules";
    }


    /**
     * Возврат всех правил для таблицы в виде JSON (таблица на JS)
     *
     * @return всех правил в виде JSON
     */
    @GetMapping(path = "api/rulesForTable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(RuleViews.forTable.class)
    public ResponseEntity<List<Rule>> getRulesForTable() {
        return ResponseEntity.ok(ruleService.findAllByOrderByIdAsc());
    }

    @PreAuthorize("hasAuthority('Администратор')")
    @GetMapping("/rule")
    public String newRule(Model model) {
        clientsForEditableRule.clear();
        model.addAttribute("users", userService.findAll());
        model.addAttribute("rule", new Rule());
        model.addAttribute("allDays", Days.values());
        return "rule";
    }


    @PreAuthorize("hasAuthority('Администратор')")
    @GetMapping("/rule/{id}")
    public String getEditPage(@PathVariable Long id, Model model) {
        Rule rule = ruleService.getOneRowById(id);
        model.addAttribute("rule", rule);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allDays", Days.values());
        clientsForEditableRule = rule.getClients();
        return "rule";
    }


    @GetMapping("/rule/view/{id}")
    public String getViewPage(@PathVariable Long id, Model model) {
        Rule rule = ruleService.getOneRowById(id);
        model.addAttribute("rule", rule);
        return "operations/view";
    }

    @PostMapping(value = "/rule")
    public String saveRule(Model model, @Valid Rule rule) {
        try {
            rule = ruleService.saveWithClients(rule, clientsForEditableRule);
        } catch (ManagerIsNull | TimeIncorrect exception) {
            model.addAttribute("badMessage", exception.getMessage());
        }
        model.addAttribute("goodMessage", "Сохранено");
        return getEditPage(rule.getId(), model);
    }


    /**
     * Удаление правил по массиву IDs
     *
     * @param ids - массив с ID правила
     */
    @DeleteMapping("/rule")
    public String deleteRule(@RequestBody List<Long> ids) {
        try {
            ids.forEach(ruleService::deleteOneRow);
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            System.err.println(exception.getMessage());
        }
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

    /**
     * это пиздец
     */
    protected void addClient(Client client) {
        clientsForEditableRule.add(client);
    }

    protected void removeClient(Client client) {
        clientsForEditableRule.remove(client);
    }
}
