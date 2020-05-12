package com.diplom.work.controller;

import com.diplom.work.exceptions.SettingsNotFound;
import com.diplom.work.core.Settings;
import com.diplom.work.core.user.User;
import com.diplom.work.svc.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAuthority('Администратор')")
public class SettingsController {
    private final SettingsService settingsService;

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping(path = "settings")
    public String showPage(Model model, @RequestParam(name = "saved", required = false) String isSaved) {
        if(isSaved != null)
            model.addAttribute("goodMessage", "Сохранено!");
        try{
            model.addAttribute("settings",settingsService.getSettings());
        }catch (SettingsNotFound exception){
            model.addAttribute("settings",new Settings());
        }
        return "settings";
    }

    @PostMapping(path = "settings")
    public String showPage(Model model, Settings settings) {
        settingsService.save(settings);
        return "redirect:/settings?saved";
    }
}
