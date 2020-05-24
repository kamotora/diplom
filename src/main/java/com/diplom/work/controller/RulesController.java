package com.diplom.work.controller;

import com.diplom.work.core.Client;
import com.diplom.work.core.Days;
import com.diplom.work.core.Rule;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.ManagerIsNull;
import com.diplom.work.exceptions.TimeIncorrect;
import com.diplom.work.svc.RuleService;
import com.diplom.work.svc.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class RulesController {

    private final RuleService ruleService;
    private final UserService userService;

    @Autowired
    public RulesController(RuleService ruleService, UserService userService) {
        this.ruleService = ruleService;
        this.userService = userService;
    }

    /**
     * Страница с таблицей "Список правил маршрутизации"
     */
    @GetMapping("/rules")
    public String list(Model model) {
        return "rules";
    }


    /**
     * Возврат всех правил для таблицы в виде JSON (таблица на JS)
     *
     * @return всех правил в виде JSON
     */
    @GetMapping(path = "api/rulesForTable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.forTable.class)
    public ResponseEntity<List<Rule>> getRulesForTable() {
        return ResponseEntity.ok(ruleService.findAllByOrderByIdAsc());
    }


    /**
     * Вывод формы для добавления правила
     *
     * @return заполненная форма
     */
    @GetMapping("/rule")
    public String getPageForAddRule(Model model,  @AuthenticationPrincipal User user) {
        if(user.getFirstRoleName().equals("Пользователь")) {
            model.addAttribute("isUser", "true");
            model.addAttribute("users", user);
            Rule rule = new Rule();
            rule.setManager(user);
            model.addAttribute(rule);
        }
        else {
            model.addAttribute("users", userService.findAll());
            model.addAttribute("rule", new Rule());
        }
        model.addAttribute("allDays", Days.values());
        return "rule";
    }


    /**
     * Вывод формы для изменения правила
     *
     * @return заполненная форма
     */

    @GetMapping("/rule/{id}")
    public String getPageForEditRule(@PathVariable("id") Rule rule, Model model, @AuthenticationPrincipal User user) {
        if(user.getFirstRoleName().equals("Пользователь"))
            model.addAttribute("isUser", "true");
        model.addAttribute("rule", rule);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allDays", Days.values());
        return "rule";
    }


    /**
     * Страница для просмотра правила
     *
     * @return страница
     */
    @GetMapping("/rule/{id}/view")
    public String getViewPage(@PathVariable("id") Rule rule, Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("rule", rule);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allDays", Days.values());
        model.addAttribute("isView", "true");
        if(user.getFirstRoleName().equals("Пользователь"))
            model.addAttribute("isUser", "true");
        return "rule";
    }

    /**
     * Сохранение правила
     *
     * @return страница с заполенной формой и сообщение об ошибке/успехе
     */
    @PostMapping(value = "/rule")
    public String saveRule(Model model, Rule rule, @AuthenticationPrincipal User user) {
        try {
            rule = ruleService.save(rule);
            model.addAttribute("goodMessage", "Сохранено");
        } catch (TimeIncorrect timeIncorrect) {
            model.addAttribute("badMessage", "Дни/время указаны неверно");
        } catch (ManagerIsNull managerIsNull) {
            model.addAttribute("badMessage", "Выберите менеджера или укажите 'Умная маршрутизация'");
        }
        return getPageForEditRule(rule, model, user);
    }


    /**
     * Удаление правил по массиву IDs
     *
     * @param ids - массив с ID правила
     */
    @DeleteMapping("/rule")
    public String deleteRule(Model model, @RequestBody List<Long> ids) {
        try {
            ids.forEach(ruleService::deleteOneRow);
            model.addAttribute("goodMessage", "Удалено");
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            System.err.println(exception.getMessage());
            model.addAttribute("badMessage", "Не удалось удалить!");
        }
        return "rules :: messages";
    }

}
