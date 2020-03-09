package com.diplom.work;

import com.codeborne.selenide.*;
import com.diplom.work.core.user.User;
import com.diplom.work.repo.UserRepository;
import com.diplom.work.svc.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

@SpringBootTest
public class TestRegistration {
    @Autowired
    private UserRepository userRepository;

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
        Selenide.open("http://localhost:2345/registration");
        Selenide.sleep(3000);
        $("h1").shouldHave(Condition.text("Регистрация"));
    }
    @AfterEach
    public void closePage(){
        Selenide.closeWindow();
    }

    /**
     * Проверяем поведение на различные пароли при регистрации
     * */
    @Test
    public void TestRegistrationWithNotMatchPasswords(){
        $(byName("username")).setValue("Test");
        $(byName("password1")).setValue("Test");
        $(byName("password2")).setValue("Test1");
        $(byValue("USER")).click();
        $(byValue("Регистрация")).click();
        $((".message")).shouldBe(Condition.text("Пустой логин или пароль, или пароли не совпадают"));
    }

    /**
     * Проверяем поведение при невыбранной роли пользователя
     * */
    @Test
    public void TestRegistrationWithNotSelectedRole(){
        $(byName("username")).setValue("Test");
        $(byName("password1")).setValue("Test");
        $(byName("password2")).setValue("Test");
        $(byValue("Регистрация")).click();
        $((".message")).shouldBe(Condition.text("Роль/Роли не выбраны"));
    }

    /**
     * Проверяем поведение при пустой форме
     * */
    @Test
    public void TestRegistrationWithEmptyForm(){
        $(byValue("Регистрация")).click();
        $((".message")).shouldBe(Condition.text("Пустой логин или пароль, или пароли не совпадают"));
    }

    /**
     * Проверяем возможность регистрации при верных данных
     * */
    @Test
    public void TestRegistrationSuccessAndLogin(){
        final String USER_NAME = "Test";
        $(byName("username")).setValue(USER_NAME);
        //Для простоты пароль = логин
        $(byName("password1")).setValue(USER_NAME);
        $(byName("password2")).setValue(USER_NAME);
        $(byValue("USER")).click();
        $(byValue("Регистрация")).click();

        //После регистрации кидает на страницу входа
        $("h1").shouldHave(Condition.text("Вход"));
        //Проверим наличие пользователя в базе
        User user =  userRepository.findByUsername(USER_NAME);
        Assert.assertNotNull(user);
        //Пытаемся войти
        $(byName("username")).setValue(USER_NAME);
        $(byName("password")).setValue(USER_NAME);
        $(byValue("Войти")).click();
        //Если появилась кнопка выхода - зашли успешно
        $(byValue("Выход")).shouldBe(Condition.exist);

        // Чистим за собой
        // Обнуляем роли
        user.setRoles(new HashSet<>());
        userRepository.save(user);
        userRepository.delete(user);
    }
}
