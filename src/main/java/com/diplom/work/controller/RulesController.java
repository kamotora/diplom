package com.diplom.work.controller;

import com.diplom.work.core.Client;
import com.diplom.work.core.Days;
import com.diplom.work.core.Rule;
import com.diplom.work.core.Settings;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.ManagerIsNull;
import com.diplom.work.exceptions.TimeIncorrect;
import com.diplom.work.svc.RuleService;
import com.diplom.work.svc.SettingsService;
import com.diplom.work.svc.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@Slf4j
public class RulesController {
    private final RuleService ruleService;
    private final UserService userService;
    private final SettingsService settingsService;

    private static final String USERS_ATTRUBUTE_NAME = "users";
    private static final String IS_USER_ATTRIBUTE_NAME = "isUser";
    private static final String GOODMESSAGE_ATTRIBUTE_NAME = "goodMessage";
    private static final String BADMESSAGE_ATTRIBUTE_NAME = "badMessage";
    public static final String ALL_DAYS_ATTRIBUTE_NAME = "allDays";
    @Autowired
    public RulesController(RuleService ruleService, UserService userService, SettingsService settingsService) {
        this.ruleService = ruleService;
        this.userService = userService;
        this.settingsService = settingsService;
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
    @GetMapping(path = "/rule/table", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.ForTable.class)
    public ResponseEntity<List<Rule>> getAllRules(@AuthenticationPrincipal User user) {
        // Смотрим настройки
        boolean isUserCanViewOnlyTheirRules = settingsService.getSettingsOptional()
                .map(Settings::getIsUsersCanViewOnlyTheirRules).orElse(false);
        // Если нужно, то показываем только правила с участием менеджера
        if (isUserCanViewOnlyTheirRules && user.getRoles().contains(Role.USER)) {
            return ResponseEntity.ok(ruleService.getRulesForUserAndSmartRules(user));
        }
        return ResponseEntity.ok(ruleService.getAll());
    }

    /**
     * Вывод формы для добавления правила
     *
     * @return заполненная форма
     */
    @GetMapping("/rule")
    public String getPageForAddRule(Model model, @AuthenticationPrincipal User user) {
        // Смотрим настройки
        boolean isUserCanAddInRuleOnlyMyself = settingsService.getSettingsOptional()
                .map(Settings::getIsUsersCanAddRulesOnlyMyself).orElse(false);
        // Если нужно, автоматом добавляем менеджера
        if (isUserCanAddInRuleOnlyMyself && user.getRoles().contains(Role.USER)) {
            model.addAttribute(IS_USER_ATTRIBUTE_NAME, "true");
            model.addAttribute(USERS_ATTRUBUTE_NAME, user);
            Rule rule = new Rule();
            rule.setManager(user);
            model.addAttribute(rule);
        } else {
            model.addAttribute(USERS_ATTRUBUTE_NAME, userService.findAll());
            model.addAttribute("rule", new Rule());
        }
        model.addAttribute(ALL_DAYS_ATTRIBUTE_NAME, Days.values());
        return "rule";
    }


    /**
     * Вывод формы для изменения правила
     *
     * @return заполненная форма
     */

    @GetMapping("/rule/{id}")
    public String getPageForEditRule(@PathVariable("id") Rule rule, Model model, @AuthenticationPrincipal User user) {
        // Смотрим настройки
        boolean isUserCanAddInRuleOnlyMyself = settingsService.getSettingsOptional()
                .map(Settings::getIsUsersCanAddRulesOnlyMyself).orElse(false);
        if (isUserCanAddInRuleOnlyMyself && user.getRoles().contains(Role.USER)) {
            model.addAttribute(IS_USER_ATTRIBUTE_NAME, "true");
        }
        model.addAttribute("rule", rule);
        model.addAttribute(USERS_ATTRUBUTE_NAME, userService.findAll());
        model.addAttribute(ALL_DAYS_ATTRIBUTE_NAME, Days.values());
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
        model.addAttribute(USERS_ATTRUBUTE_NAME, userService.findAll());
        model.addAttribute(ALL_DAYS_ATTRIBUTE_NAME, Days.values());
        model.addAttribute("isView", "true");
        // Смотрим настройки
        boolean isUserCanAddInRuleOnlyMyself = settingsService.getSettingsOptional()
                .map(Settings::getIsUsersCanAddRulesOnlyMyself).orElse(false);
        if (isUserCanAddInRuleOnlyMyself && user.getRoles().contains(Role.USER)) {
            model.addAttribute(IS_USER_ATTRIBUTE_NAME, "true");
        }
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
            model.addAttribute(GOODMESSAGE_ATTRIBUTE_NAME, "Сохранено");
        } catch (TimeIncorrect timeIncorrect) {
            model.addAttribute(BADMESSAGE_ATTRIBUTE_NAME, "Дни/время указаны неверно");
        } catch (ManagerIsNull managerIsNull) {
            model.addAttribute(BADMESSAGE_ATTRIBUTE_NAME, "Выберите менеджера или укажите 'Умная маршрутизация'");
        }
        return getPageForEditRule(rule, model, user);
    }

    /**
     * Возврат всех клиентов для правила
     *
     * @return всех клиентов в виде JSON
     */
    @GetMapping(path = "/rule/{id}/clients", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.ForTable.class)
    public ResponseEntity<Set<Client>> getUsersForTable(@PathVariable("id") Rule rule) {
        return ResponseEntity.ok(rule.getClients());
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
            model.addAttribute(GOODMESSAGE_ATTRIBUTE_NAME, "Удалено");
        } catch (Exception exception) {
            log.error("Ошибка при удалении правила: {}",exception.getMessage());
            model.addAttribute(BADMESSAGE_ATTRIBUTE_NAME, "Не удалось удалить!");
        }
        return "fragments/messages :: messages";
    }

}
