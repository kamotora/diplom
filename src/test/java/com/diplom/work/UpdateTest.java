package com.diplom.work;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.Test;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class UpdateTest {

    @Test
    public void TestUpdate() {

        /**
         *
         * Аналогично с операцией добавления
         */
        open("http://localhost:2345/");
        $(byName("username")).setValue("gerbert");
        $(byName("password")).setValue("12345").pressEnter();
        $(byName("ред")).click();
        Selenide.sleep(3000);
        $(byName("client")).setValue("88005553535");
        Selenide.sleep(3000);
        $(byName("number")).setValue("205");
        Selenide.sleep(3000);
        $(byName("FIOClient")).setValue("Хаустов Герберт Игоревич");
        $(byText("Принять")).click();
        $(byText("Ред.")).shouldBe(Condition.visible);

    }

    @Test
    public void TestUpdateWithWrongAnswer(){
        /**
         *
         * некоректные данные
         */

        Selenide.sleep(3000);
        $(byName("ред")).click();
        Selenide.sleep(3000);
        $(byName("client")).setValue("880055535");
        Selenide.sleep(3000);
        $(byName("number")).setValue("205");
        Selenide.sleep(3000);
        $(byName("FIOClient")).setValue("Хаустов Герберт Игоревич");
        $(byText("Принять")).click();
        $(byText("Ред.")).shouldNotBe(Condition.visible);


    }
}
