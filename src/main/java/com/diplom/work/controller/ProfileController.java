package com.diplom.work.controller;

import com.diplom.work.core.dto.UserDto;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.NewPasswordsNotEquals;
import com.diplom.work.exceptions.OldPasswordsNotEquals;
import com.diplom.work.svc.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Показать страницу "Профиль" для текущего пользователя
     *
     * @param user текущий пользователь
     * @return сообщение об усхепе/ошибке
     */
    @GetMapping(path = "profile")
    public String showPage(Model model, @AuthenticationPrincipal User user
            , @RequestParam(name = "saved", required = false) String isSaved) {
        if (isSaved != null)
            model.addAttribute("goodMessage", "Сохранено!");
        model.addAttribute("user", new UserDto(user));
        return "profile";
    }

    /**
     * Смена пароля для текущего пользователя
     *
     * @param formUser текущий пароль, новый пароль 2 раза
     * @param user     текущий пользователь
     * @return сообщение об усхепе/ошибке
     */
    @PostMapping(path = "profile/changePass", consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> changePass(@RequestBody UserDto formUser, @AuthenticationPrincipal User user) {
        try {
            userService.changePassword(user, formUser);
        } catch (OldPasswordsNotEquals | NewPasswordsNotEquals exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        return ResponseEntity.ok("Сохранено!");
    }
}
