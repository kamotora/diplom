package com.diplom.work;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.repo.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Проверка прав доступа к различным функциям сайта
 */
@SpringBootTest
public class RoleUsersTest {
    @Autowired
    private UserRepository userRepository;

    private static User admin;
    private static User simpleUser;
    private static final String ADMIN_LOGIN = "admin_test";
    private static final String USER_LOGIN = "user_test";
    private static final String SIMPLE_PASSWORD = "123456";

    @BeforeAll
    public static void configAndCreateUsers(){
        Configuration.browser = "chrome";
        Configuration.screenshots = false;
        Configuration.timeout = 10000;
        Configuration.fastSetValue = false;
        Configuration.holdBrowserOpen = false;
    }

    @BeforeEach
    public void openLoginForm(){
        //Создаём пользователей для проверки прав доступа
        // Не статически, т.к. Autowired
        admin = new User();
        admin.setUsername(ADMIN_LOGIN);
        admin.setPasswordAndEncrypt(SIMPLE_PASSWORD);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(Role.ADMIN);
        admin.setRoles(roleSet);
        admin.setActive(true);
        userRepository.save(admin);

        simpleUser = new User();
        simpleUser.setActive(true);
        simpleUser.setUsername(USER_LOGIN);
        simpleUser.setPasswordAndEncrypt(SIMPLE_PASSWORD);
        roleSet = new HashSet<>();
        roleSet.add(Role.USER);
        simpleUser.setRoles(roleSet);
        userRepository.save(simpleUser);

        //Проверяем, правда ли добавились
        Assertions.assertNotNull(userRepository.findByUsername(ADMIN_LOGIN));
        Assertions.assertNotNull(userRepository.findByUsername(USER_LOGIN));


        Selenide.open("http://localhost:2345/login");
        Selenide.sleep(3000);
        //Есть ли кнопка
        $(byValue("Войти")).should(Condition.exist);
    }

    @AfterEach
    public void closeBrowser(){
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
        Selenide.closeWindow();

        userRepository.delete(simpleUser);
        userRepository.delete(admin);
        //Проверяем, правда ли удалились
        Assertions.assertNull(userRepository.findByUsername(ADMIN_LOGIN));
        Assertions.assertNull(userRepository.findByUsername(USER_LOGIN));
    }

    /**
     * Проверяем возможность совершения действий (добавить, изменить, удалить)
     * с правилами маршрутизации от лица админа
     * */
    @Test
    public void testActionsWithRulesByAdmin(){

        //Входим под админом
        $(byName("username")).setValue(ADMIN_LOGIN);
        $(byName("password")).setValue(SIMPLE_PASSWORD);
        $(byValue("Войти")).click();

        //Если появилась кнопка выхода - зашли успешно
        $(byValue("Выход")).should(Condition.exist);

        $(byText("Добавить")).click();
        //Появилась ли форма ?
        $(byName("input_form")).should(Condition.exist);
        $(byName("client")).should(Condition.exist);
        $(byName("number")).should(Condition.exist);
        $(byName("FIOClient")).should(Condition.exist);

        //Переходим на домашнюю и пытаемся изменить какую-нибудь запись
        Selenide.open("http://localhost:2345");

        $(".fa-edit").should(Condition.exist);
        $(".fa-edit").click();

        //Появилась ли форма ?
        $(byName("edit_form")).should(Condition.exist);
        //Поля должны быть заполнены
        Assertions.assertNotEquals($(byName("client")).getValue(), "");
        Assertions.assertNotEquals($(byName("number")).getValue(), "");
        Assertions.assertNotEquals($(byName("FIOClient")).getValue(), "");
        //Переходим на домашнюю и пытаемся выйти
        Selenide.open("http://localhost:2345");
        $(byValue("Выход")).click();
        $(byText("Вы вышли")).should(Condition.exist);
    }

    /**
     * Проверяем возможность совершения действий (изменить, удалить)
     * с логами от лица админа
     * */
    @Test
    public void testActionsWithLogsByAdmin(){

        //Входим под админом
        $(byName("username")).setValue(ADMIN_LOGIN);
        $(byName("password")).setValue(SIMPLE_PASSWORD);
        $(byValue("Войти")).click();

        //Если появилась кнопка выхода - зашли успешно
        $(byValue("Выход")).should(Condition.exist);

        $(byLinkText("Логи")).click();
        //Появились ли логи?
        $(byText("Время")).should(Condition.exist);
        $(byText("idСессии")).should(Condition.exist);
        Selenide.open("http://localhost:2345/logs");
        $(".fa-edit").should(Condition.exist);
        $$(".fa-edit").first().click();
        //Появилась ли форма ?
        $(by("action","/updateLogs")).should(Condition.exist);
        //Поля должны быть заполнены
        Assertions.assertNotEquals($(byName("session_id")).getValue(), "");
        Assertions.assertNotEquals($(byName("type")).getValue(), "");
        Assertions.assertNotEquals($(byName("from_number")).getValue(), "");
        //Переходим на домашнюю и пытаемся выйти
        Selenide.open("http://localhost:2345");
        $(byValue("Выход")).click();
        $(byText("Вы вышли")).should(Condition.exist);
    }


    /**
     * Проверяем возможность совершения действий (добавить, изменить, удалить)
     * с правилами маршрутизации от лица обычного пользователя
     * */
    @Test
    public void testActionsWithRulesByUser(){

        //Входим под user
        $(byName("username")).setValue(USER_LOGIN);
        $(byName("password")).setValue(SIMPLE_PASSWORD);
        $(byValue("Войти")).click();

        //Если появилась кнопка выхода - зашли успешно
        $(byValue("Выход")).should(Condition.exist);

        $(byText("Добавить")).click();
        //Появилась ли ошибка

        $("h1").shouldHave(Condition.text("HTTP Status 403"));

        //Переходим на домашнюю и пытаемся изменить какую-нибудь запись
        Selenide.open("http://localhost:2345");

        $(".fa-edit").should(Condition.exist);
        $(".fa-edit").click();

        //Появилась ли ошибка ?
        $("h1").shouldHave(Condition.text("HTTP Status 403"));

        //Переходим на домашнюю и пытаемся удалить какую-нибудь запись
        Selenide.open("http://localhost:2345");

        $(".fa-trash").should(Condition.exist);
        $(".fa-trash").click();

        //Появилась ли ошибка ?
        $("h1").shouldHave(Condition.text("HTTP Status 403"));

        //Переходим на домашнюю и пытаемся выйти
        Selenide.open("http://localhost:2345");
        $(byValue("Выход")).click();
        $(byText("Вы вышли")).should(Condition.exist);
    }

    /**
     * Проверяем возможность совершения действий (изменить, удалить)
     * с правилами маршрутизации от лица обычного пользователя (может только смотреть)
     * */
    @Test
    public void testActionsWithLogsByUser(){

        //Входим под user
        $(byName("username")).setValue(USER_LOGIN);
        $(byName("password")).setValue(SIMPLE_PASSWORD);
        $(byValue("Войти")).click();

        //Если появилась кнопка выхода - зашли успешно
        $(byValue("Выход")).should(Condition.exist);

        $(byLinkText("Логи")).click();
        //Появились ли логи?
        $(byText("Время")).should(Condition.exist);
        $(byText("idСессии")).should(Condition.exist);
        Selenide.open("http://localhost:2345/logs");
        $(".fa-edit").should(Condition.exist);
        $(".fa-edit").click();

        $("h1").shouldHave(Condition.text("HTTP Status 403"));

        //Переходим на страницу с логами и пытаемся удалить какую-нибудь запись
        Selenide.open("http://localhost:2345/logs");

        $(".fa-trash").should(Condition.exist);
        $(".fa-edit").click();

        //Появилась ли ошибка ?
        $("h1").shouldHave(Condition.text("HTTP Status 403"));

        //Переходим на домашнюю и пытаемся выйти
        Selenide.open("http://localhost:2345");
        $(byValue("Выход")).click();
        $(byText("Вы вышли")).should(Condition.exist);
    }

}
