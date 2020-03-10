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
public class EditTest {

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

    /**
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
        $(byName("client")).setValue("88005553595");
        Selenide.sleep(3000);
        $(byName("number")).setValue("205");
        Selenide.sleep(3000);
        $(byName("FIOClient")).setValue("Хаустов Герберт Игоревич");
        Selenide.sleep(3000);
        $(byText("Принять")).click();
        Selenide.sleep(3000);

        /**
         * Если всё правильно попадаем на главную страницу
         */

        $(byText("Ред.")).shouldBe(Condition.visible);

        /**
         * Удалим добавленную запись
         */

        Rule rule = ruleRepository.findByClientNumber("88005553595");
        ruleRepository.delete(rule);

        /**
         * Проверим что удалили
         */

        $(byName("88005553595")).shouldNotBe(Condition.visible);

    }

    /**
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