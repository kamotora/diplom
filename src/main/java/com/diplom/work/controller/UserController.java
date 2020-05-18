package com.diplom.work.controller;

import com.diplom.work.core.dto.UserEditDto;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.NewPasswordsNotEquals;
import com.diplom.work.exceptions.UsernameAlreadyExist;
import com.diplom.work.svc.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
     */
    @GetMapping(path = "users")
    public String usersPage(Model model) {
        model.addAttribute("adminRole", Role.ADMIN);
        model.addAttribute("userRole", Role.USER);
        return "users";
    }


    /**
     * Возврат всех пользователей для таблицы в виде JSON (таблица на JS)
     *
     * @return всех пользователи в виде JSON
     */
    @GetMapping(path = "users/table", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.forTable.class)
    public ResponseEntity<List<User>> getUsersForTable() {
        return ResponseEntity.ok(userService.findAll());
    }


    /**
     * Страница для добавление пользователя
     */
    @GetMapping("user")
    public String userAddForm(Model model) {
        initPage(model, new UserEditDto());
        return "user";
    }

    /**
     * Страница для редактирования
     *
     * @param user - редактируемые пользователь
     */
    @GetMapping("user/{id}")
    public String userEditForm(@PathVariable("id") User user, Model model) {
        initPage(model, new UserEditDto(user));
        return "user";
    }

    /**
     * Удаление пользователя по ID
     *
     * @param id - ID пользователя
     */
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
     * Удаление пользователей по массиву IDs
     *
     * @param ids - массив с ID пользователей
     */
    @DeleteMapping("user")
    public String deleteUser(@RequestBody List<Long> ids) {
        try {
            ids.forEach(userService::deleteUserById);
        } catch (UsernameNotFoundException exception) {
            //Хз что ответить)
        }
        return "redirect:/users";
    }

    /**
     * Сохранение изменённого или добавление нового пользователя
     *
     * @param user - данные с формы
     */
    @PostMapping("user")
    public String saveUser(@Valid UserEditDto user, Model model) {
        try {
            userService.save(user);
        } catch (UsernameAlreadyExist | NewPasswordsNotEquals exception) {
            initPage(model, user);
            model.addAttribute("badMessage", exception.getMessage());
            return "user";
        }
        model.addAttribute("goodMessage", "Сохранено!");
        initPage(model, user);
        return "user";
    }

    /**
     * Добавление параметров на страницу для добавления/изменения
     *
     * @param user - что отобразить на форме и куда данные с формы запишутся
     */
    private void initPage(Model model, UserEditDto user) {
        model.addAttribute("allRoles", Role.values());
        model.addAttribute("user", user);
    }
}