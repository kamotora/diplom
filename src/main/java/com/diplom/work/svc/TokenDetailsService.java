package com.diplom.work.svc;

import com.diplom.work.core.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TokenDetailsService implements UserDetailsService {
    private final SettingsService settingsService;
    private final UserService userService;

    @Autowired
    public TokenDetailsService(SettingsService settingsService, UserService userService) {
        this.settingsService = settingsService;
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String token) {
        Settings settings = settingsService.getSettingsOptional()
                .orElseThrow(() -> new UsernameNotFoundException("Настройки не настроены"));

        if (settings.getIsTokensActivate() != null && Boolean.TRUE.equals(settings.getIsTokensActivate()))
            return userService.findByToken(token)
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователя с таким токеном не существует!"));
        else
            throw new UsernameNotFoundException("Авторизация через логин запрещена");
    }
}
