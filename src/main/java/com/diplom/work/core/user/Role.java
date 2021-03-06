package com.diplom.work.core.user;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER("Пользователь"), ADMIN("Администратор"), STATIST("Статист");
    private final String name;
    Role(String name) {
        this.name = name;
    }
    @Override
    public String getAuthority() {
        return name;
    }

}
