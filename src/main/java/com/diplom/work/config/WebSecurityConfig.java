package com.diplom.work.config;

import com.diplom.work.core.user.User;
import com.diplom.work.svc.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Авторизация
 * */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;
    /**
     * Какие страницы доступны всем, а какие только после входа
     * */
    //TODO logout не работает (
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("static","/login", "/registration").permitAll() // Доступны всем
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")  // Доступны после входа
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")// Выход доступен всем
                .permitAll();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(User.TYPE_ENCRYPT);
    }
}