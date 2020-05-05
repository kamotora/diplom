package com.diplom.work.controller;
import com.diplom.work.core.dto.UserEditDto;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class ControllerUtils {
    static Map<String, String> getErrors(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );
        return bindingResult.getFieldErrors().stream().collect(collector);
    }

    static void InitNavBar(User currentUser, Model model){
        model.addAttribute("currentUser", currentUser);
    }
}
