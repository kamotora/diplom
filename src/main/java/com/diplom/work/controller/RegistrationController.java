package com.diplom.work.controller;

import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/registration")
    public String registration(Map<String, Object> model) {
        model.put("roles",Role.values());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, @RequestParam List<String> roles, @RequestParam String password1, @RequestParam String password2, Map<String, Object> model) {
        User userFromDb = userRepo.findByUsername(user.getUsername());

        if (userFromDb != null) {
            model.put("message", "Такой пользователь есть");
            return "registration";
        }
        if(user.getUsername().isEmpty() || password1 == null || password1.isEmpty() || password2 == null || password2.isEmpty() || !password1.equals(password2)){
            model.put("message","Пустой логин или пароль, или пароли не совпадают");
            return "registration";
        }
        if(roles == null || roles.size() == 0) {
            model.put("message","Роль/Роли не выбраны");
            return "registration";
        }
        Set<Role> roleSet = new HashSet<>();
        roles.forEach(role -> roleSet.add(Role.valueOf(role)));
        user.setRoles(roleSet);
        user.setPassword(User.TYPE_ENCRYPT.encode(password1));
        user.setActive(true);
        userRepo.save(user);

        return "redirect:/login";
    }
}