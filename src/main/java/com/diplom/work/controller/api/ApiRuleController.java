package com.diplom.work.controller.api;

import com.diplom.work.core.Rule;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.ManagerIsNull;
import com.diplom.work.exceptions.TimeIncorrect;
import com.diplom.work.svc.RuleService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD для правил маршрутизации через json запросы
 * (мб использовано для удалённого редактированния)
 */

@RestController
@RequestMapping("rest/rule")
public class ApiRuleController {

    private final RuleService ruleService;

    @Autowired
    public ApiRuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * Возврат всех правил для в виде JSON
     *
     * @return все клиенты в виде JSON
     */
    @RequestMapping(path = "/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.allRule.class)
    public List<Rule> getAllRules() {
        return ruleService.getAll();
    }

    /**
     * Добавление нового правила
     *
     * @param rule добавляемое правило в виде json
     * @return Добавленное правило с id
     * @see Rule
     */
    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Rule addRule(@RequestBody Rule rule) throws TimeIncorrect, ManagerIsNull {
        return ruleService.save(rule);
    }


    /**
     * Обновление информации о правиле
     *
     * @param rule правило в виде json
     * @return Изменённое правило
     * @see Rule
     */
    @PutMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Rule updateRule(@RequestBody Rule rule) {
        return ruleService.updateExistingRule(rule);
    }


    /**
     * Удаление информации о правиле по его id
     *
     * @param id - id правила
     * @return true - удалено, false - ошибка
     * @see Rule
     */
    @DeleteMapping(path = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public boolean deleteRule(@PathVariable("id") Long id) {
        return ruleService.deleteOneRow(id);
    }

    /**
     * Удаление информации о правилах по массиву id
     *
     * @param ids id удаляемых правил
     * @return true - удалено, false - ошибка
     * @see Rule
     */
    @DeleteMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public boolean deleteRules(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            ruleService.deleteOneRow(id);
        }
        return true;
    }
}
