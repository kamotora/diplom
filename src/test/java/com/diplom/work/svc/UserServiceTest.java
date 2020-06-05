package com.diplom.work.svc;

import com.diplom.work.core.dto.UserEditDto;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.exceptions.NewPasswordsNotEquals;
import com.diplom.work.exceptions.UsernameAlreadyExist;
import com.diplom.work.repo.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final TokenService tokenService = mock(TokenService.class);
    private final UserService userService = new UserService(userRepository, passwordEncoder, tokenService);

    @Test
    public void save() {
        UserEditDto dto = new UserEditDto();
        final String PASSWORD = "pass";
        final String USERNAME = "user";
        dto.setUsername(USERNAME);
        dto.setPassword1(PASSWORD);
        dto.setPassword2(PASSWORD);
        dto.setRole(Role.USER);
        dto.setActive(true);
        User user = null;
        try {
            user = userService.save(dto);
        } catch (NewPasswordsNotEquals | UsernameAlreadyExist exception) {
            exception.printStackTrace();
            fail(exception.getMessage());
        }
        assertTrue(user.isActive());
        assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        assertEquals("user", user.getUsername());
        assertTrue(CoreMatchers.is(user.getPassword()).matches(passwordEncoder.encode(PASSWORD)));
    }

    @Test
    public void loadUserByUsername() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("incorrect"));
        Mockito.doReturn(new User())
                .when(userRepository)
                .findByUsername("admin");
        assertNotNull(userService.loadUserByUsername("admin"));
    }
}