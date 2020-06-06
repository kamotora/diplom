package com.diplom.work.controller;

import com.diplom.work.core.Settings;
import com.diplom.work.exceptions.SettingsNotFound;
import com.diplom.work.svc.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAuthority('Администратор')")
public class SettingsController {
    private final SettingsService settingsService;
    private static final String SETTINGS_ATTRIBUTE_NAME = "settings";
    private static final String SETTINGS_PAGE_NAME = SETTINGS_ATTRIBUTE_NAME;

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping(path = "settings")
    public String showPage(Model model, @RequestParam(name = "saved", required = false) String isSaved) {
        if (isSaved != null)
            model.addAttribute("goodMessage", "Сохранено!");
        try {
            model.addAttribute(SETTINGS_ATTRIBUTE_NAME, settingsService.getSettings());
        } catch (SettingsNotFound exception) {
            model.addAttribute(SETTINGS_ATTRIBUTE_NAME, new Settings());
        }
        return SETTINGS_PAGE_NAME;
    }

    @PostMapping(path = "settings")
    public String showPage(Model model, Settings settings) {
        settingsService.save(settings);
        return "redirect:/"+SETTINGS_PAGE_NAME+"?saved";
    }
}
