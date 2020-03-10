package com.diplom.work;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;


import com.codeborne.selenide.Configuration;
import org.springframework.boot.test.context.SpringBootTest;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

@SpringBootTest
public class EditTest {

    /**
     *
     * Проверка на добавление
     */
    @Test
    public void TestEdit() {
        open("http://localhost:2345/");
        $(byName("username")).setValue("gerbert");
        $(byName("password")).setValue("12345").pressEnter();
        Selenide.sleep(3000);
        $(byText("Добавить")).click();
        Selenide.sleep(3000);
        $(byName("client")).setValue("88005553535");
        Selenide.sleep(3000);
        $(byName("number")).setValue("205");
        Selenide.sleep(3000);
        $(byName("FIOClient")).setValue("Хаустов Герберт Игоревич");
        Selenide.sleep(3000);
        $(byText("Принять")).click();
        Selenide.sleep(3000);

        /**
         *
         * Если всё правильно попадаем на главную страницу
         */

        $(byText("Ред.")).shouldBe(Condition.visible);
    }

    /**
     *
     * При следующих данных добавления произойти не должно
     */

    @Test
    public void EditWithWrongAnswer(){
        $(byText("Добавить")).click();
        $(byName("client")).setValue("880055535");
        $(byName("number")).setValue("20");
        $(byName("FIOClient")).setValue("Хаустов Герберт Игоревич");
        //не увидим элемент главной страницы
        $(byText("Ред.")).shouldNotBe(Condition.visible);
    }


}
