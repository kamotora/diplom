package com.diplom.work;

import com.codeborne.selenide.*;
import com.diplom.work.core.Rule;
import com.diplom.work.repo.RuleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

@SpringBootTest
public class UpdateTest {

    @Autowired
    RuleRepository ruleRepository;

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
        Selenide.sleep(3000);
    }

    @Test
    public void TestUpdate() {

        /**
         *
         * Аналогично с операцией добавления
         */

        /**
         * Новые значения которые будем присваивать
         */
        String newClientNumber = "99999999999";
        String newManagerNumber = "999";
        String newFIOClient = "Петров Петр Петрович";


        /**
         * Старые значения
         */

        String oldClientNumber = "";
        String oldManagetNumber = "";
        String oldFIOClient = "";

        open("http://localhost:2345/");
        $(byName("username")).setValue("gerbert");
        $(byName("password")).setValue("12345").pressEnter();
        $(byName("ред")).click();
        Selenide.sleep(3000);

        /**
         * Таким образом запоминаем старые значения
         */
        oldClientNumber = $(byName("client")).val();
        $(byName("client")).setValue(newClientNumber);
        Selenide.sleep(3000);

        oldManagetNumber = $(byName("number")).val();
        $(byName("number")).setValue(newManagerNumber);
        Selenide.sleep(3000);

        oldFIOClient = $(byName("FIOClient")).val();
        $(byName("FIOClient")).setValue(newFIOClient);
        $(byText("Принять")).click();

        /**
         * Если удачно то должны увидеть элемент главного экрана
         */
        $(byText("Ред.")).shouldBe(Condition.visible);

        /**
         * Вернём старые значения
         */
        Rule rule = ruleRepository.findByClientNumber(newClientNumber);
        rule.setClientNumber(oldClientNumber);
        rule.setManagerNumber(oldManagetNumber);
        rule.setClientName(oldFIOClient);

        ruleRepository.save(rule);
        Selenide.sleep(3000);
        /**
         * Проверим всё ли исправили
         */

        $(byName(oldClientNumber)).shouldNotBe(Condition.visible);

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
