package com.diplom.work.controller;

import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WorkApplicationController {

    @PostMapping("/home")
    public String homePageAfterLogin(Model model) {
        return "redirect:/";
    }

    @GetMapping("/")
    public String temp(@AuthenticationPrincipal User user) {
        if(user.getRoles().contains(Role.STATIST))
            return "redirect:/logs";
        return "redirect:/rules";
    }
}
