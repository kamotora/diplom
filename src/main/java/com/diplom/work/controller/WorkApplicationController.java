package com.diplom.work.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WorkApplicationController {

    @PostMapping("/home")
    public String homePageAfterLogin(Model model){
        return "redirect:/";
    }

    //TODO удалить потом
    @GetMapping("/rules")
    public String temp(Model model){
        return "redirect:/";
    }
}
