package com.diplom.work.controller;

import com.diplom.work.controller.api.exceptions.NewPasswordsNotEquals;
import com.diplom.work.controller.api.exceptions.UsernameAlreadyExist;
import com.diplom.work.core.dto.UserEditDto;
import com.diplom.work.core.json.view.UserViews;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.svc.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@PreAuthorize("hasAuthority('Администратор')")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Страница со всеми пользователями
     * @param currentUser - пользователь, который авторизовался
     * */
    @GetMapping(path = "users")
    public String usersPage(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("adminRole", Role.ADMIN);
        model.addAttribute("userRole", Role.USER);
        return "users";
    }


    /**
     * Возврат всех пользователей для таблицы в виде JSON (таблица на JS)
     * @return всех пользователи в виде JSON
     * */
    @GetMapping(path = "api/users", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(UserViews.forTable.class)
    public ResponseEntity<List<User>> getUsersForTable() {
        return ResponseEntity.ok(userService.findAll());
    }


    /**
     * Страница для добавление пользователя
     * @param currentUser - пользователь, который авторизовался
     * */
    @GetMapping("user")
    public String userAddForm(Model model, @AuthenticationPrincipal User currentUser) {
        initPage(model, new UserEditDto(), currentUser);
        return "user";
    }

    /**
     * Страница для редактирования
     * @param currentUser - пользователь, который авторизовался
     * */
    @GetMapping("user/{id}")
    public String userEditForm(@PathVariable("id") User user, Model model, @AuthenticationPrincipal User currentUser) {
        initPage(model, new UserEditDto(user), currentUser);
        return "user";
    }

    /**
     * Удаление пользователя по ID
     * @param id - ID пользователя
     * */
    @DeleteMapping("user/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUserById(id);
        } catch (UsernameNotFoundException exception) {
            //Хз что ответить)
        }
        return "redirect:/users";
    }

    /**
     * Сохранение изменённого или добавление нового пользователя
     * @param user - данные с формы
     * @param currentUser - пользователь, который авторизовался
     * */
    @PostMapping("user")
    public String saveUser(@Valid UserEditDto user, Model model, @AuthenticationPrincipal User currentUser
    ) {
        try {
            userService.save(user);
        } catch (UsernameAlreadyExist | NewPasswordsNotEquals exception) {
            initPage(model, user, currentUser);
            model.addAttribute("message", exception.getMessage());
            return "user";
        }
        model.addAttribute("message", "Сохранено!");
        initPage(model, user, currentUser);
        return "user";
    }

    /**
     * Добавление параметров на страницу для добавления/изменения
     * @param currentUser - пользователь, который авторизовался - для нав.меню, которое разное для каждого
     * @param user - что отобразить на форме и куда данные с формы запишутся
     * */
    private void initPage(Model model, UserEditDto user, User currentUser) {
        ControllerUtils.InitNavBar(currentUser, model);
        model.addAttribute("adminRole", Role.ADMIN);
        model.addAttribute("userRole", Role.USER);
        model.addAttribute("allRoles", Role.values());
        model.addAttribute("user", user);
    }
}