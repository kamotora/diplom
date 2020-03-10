package com.diplom.work;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

@SpringBootTest
public class LoginTest {

    @BeforeAll
    public static void config(){
        Configuration.browser = "chrome";
        Configuration.screenshots = false;
        Configuration.timeout = 10000;
        Configuration.fastSetValue = false;
        Configuration.holdBrowserOpen = false;
    }
    @BeforeEach
    public void openRegistrationForm(){
        Selenide.open("http://localhost:2345/login");
        Selenide.sleep(3000);
        $("h1").shouldHave(Condition.text("Вход"));
    }
    @AfterEach
    public void closePage(){
        Selenide.closeWindow();
    }

    /**
     * Проверяем вход в личный кабинет
     * */
    @Test
    public void TestLoginPass(){
        final String USER_NAME = "admin";
        final String USER_PASSWORD = "1234";
        $(byName("username")).setValue(USER_NAME);
        $(byName("password")).setValue(USER_PASSWORD);
        $(byValue("Войти")).click();
        //Если появилась кнопка выхода - зашли успешно
        $(byValue("Выход")).shouldBe(Condition.exist);
    }

    /**
     * Проверяем вход в личный кабинет
     * */
    @Test
    public void TestLoginNotPass(){
        final String USER_NAME = "admin";
        final String USER_PASSWORD = "12345";
        $(byName("username")).setValue(USER_NAME);
        $(byName("password")).setValue(USER_PASSWORD);
        $(byValue("Войти")).click();
        //Вход не должен осуществиться
        $("h4").shouldHave(Condition.text("Неверный логин или пароль"));
    }

    /**
     * Проверяем выход из личного кабинета
     * */
    @Test
    public void TestLogOutPass(){
        final String USER_NAME = "admin";
        final String USER_PASSWORD = "1234";
        $(byName("username")).setValue(USER_NAME);
        $(byName("password")).setValue(USER_PASSWORD);
        $(byValue("Войти")).click();
        //Вход прошел успешно
        $(byValue("Выход")).shouldBe(Condition.exist);
        //нажимаем кнопку выход
        Selenide.sleep(3000);
        $(byValue("Выход")).click();
        $("h4").shouldHave(Condition.text("Вы вышли"));

    }

}

