package com.diplom.work.controller;

import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WorkApplicationController {
    @RequestMapping("/")
    public String temp(@AuthenticationPrincipal User user) {
        if (user.getRoles().contains(Role.STATIST))
            return "redirect:/logs";
        return "redirect:/rules";
    }
}
