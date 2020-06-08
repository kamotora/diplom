package com.diplom.work.controller;

import com.diplom.work.core.Settings;
import com.diplom.work.exceptions.SettingsNotFound;
import com.diplom.work.svc.SettingsService;
import com.diplom.work.svc.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.thymeleaf.util.StringUtils.isEmptyOrWhitespace;

@Controller
@PreAuthorize("hasAuthority('Администратор')")
public class SettingsController {
    private final SettingsService settingsService;
    private final UserService userService;
    private static final String SETTINGS_ATTRIBUTE_NAME = "settings";
    private static final String SETTINGS_PAGE_NAME = SETTINGS_ATTRIBUTE_NAME;
    private static final String GOODMESSAGE_ATTRIBUTE_NAME = "goodMessage";
    private static final String BADMESSAGE_ATTRIBUTE_NAME = "badMessage";

    @Autowired
    public SettingsController(SettingsService settingsService, UserService userService) {
        this.settingsService = settingsService;
        this.userService = userService;
    }

    @GetMapping(path = "settings")
    public String showPage(Model model, @RequestParam(name = "saved", required = false) String isSaved) {
        if (isSaved != null)
            model.addAttribute(GOODMESSAGE_ATTRIBUTE_NAME, "Сохранено!");
        try {
            model.addAttribute(SETTINGS_ATTRIBUTE_NAME, settingsService.getSettings());
        } catch (SettingsNotFound exception) {
            model.addAttribute(SETTINGS_ATTRIBUTE_NAME, new Settings());
        }
        return SETTINGS_PAGE_NAME;
    }

    @PostMapping(path = "settings")
    public String save(Model model, Settings settings) {
        if (isEmptyOrWhitespace(settings.getClientID())) {
            model.addAttribute(BADMESSAGE_ATTRIBUTE_NAME, "Введите идентификатор клиента");
            return SETTINGS_PAGE_NAME;
        }
        if (isEmptyOrWhitespace(settings.getClientKey())) {
            model.addAttribute(BADMESSAGE_ATTRIBUTE_NAME, "Введите ключ для подписи");
            return SETTINGS_PAGE_NAME;
        }
        // Создаём токены, если надо
        if (Boolean.TRUE.equals(settings.getIsTokensActivate()))
            userService.createTokensIfNotExists();
        settingsService.save(settings);
        return "redirect:/" + SETTINGS_PAGE_NAME + "?saved";
    }
}
