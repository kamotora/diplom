package com.diplom.work.core.dto;

import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Для представления юзера на форме
 * @see lombok.Lombok
 * */

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserEditDto {
    private Long id;
    private String username;
    private String oldPassword;
    private String password1;
    private String password2;
    private String name;
    private String number;
    private String email;
    private boolean active;
    private Role role;

    public UserEditDto(User user) {
        BeanUtils.copyProperties(user,this,"roles", "password");
        this.role = user.getFirstRole();
    }


}