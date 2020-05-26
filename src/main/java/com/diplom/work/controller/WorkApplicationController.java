package com.diplom.work.controller;

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
    public String temp(Model model) {
        return "redirect:/rules";
    }
}
