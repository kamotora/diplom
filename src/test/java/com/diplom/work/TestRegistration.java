package com.diplom.work;

import com.codeborne.selenide.Selenide;
import org.junit.Test;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class TestRegistration {

    @Test
    public void TestRegistrationWithUserTestAndPassTest(){
        Selenide.open("http://localhost:2345/");
        $(byText("Регистрация")).click();
    }
}
