package com.diplom.work.core.dto;

import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

/**
 * Для представления юзера на форме
 *
 * @see lombok.Lombok
 */

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private Long id;
    private String username;
    private String oldPassword;
    private String password1;
    private String password2;
    private String name;
    private String number;
    private String email;
    private String token;
    private boolean active;
    private Role role;

    public UserDto(User user) {
        BeanUtils.copyProperties(user, this, "roles", "password");
        if (!user.getRoles().isEmpty())
            this.role = user.getFirstRole();
    }
}
