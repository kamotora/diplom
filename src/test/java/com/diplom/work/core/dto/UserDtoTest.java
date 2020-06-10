package com.diplom.work.core.dto;

import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UserDtoTest {

    private final static String USERNAME = "admin";
    private static final String NAME = "Adminov Admin";
    private static final String EMAIL = "test@test.com";
    public static final long ID = 1L;

    @Test
    public void testEquals() {
        User user = new User();
        user.setActive(true);
        user.setEmail(EMAIL);
        user.setId(ID);
        user.setName(NAME);
        user.setUsername(USERNAME);
        user.getRoles().add(Role.ADMIN);

        UserDto userDto = new UserDto(user);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getUsername(), userDto.getUsername());

        UserDto comparableUserDto = new UserDto();
        assertNotEquals(userDto, comparableUserDto);
        comparableUserDto.setId(ID);
        comparableUserDto.setUsername(USERNAME);
        assertNotEquals(userDto, comparableUserDto);
        comparableUserDto.setEmail(EMAIL);
        comparableUserDto.setName(NAME);
        comparableUserDto.setActive(true);
        assertNotEquals(userDto, comparableUserDto);
        comparableUserDto.setRole(Role.ADMIN);
        assertEquals(userDto,comparableUserDto);
    }

}