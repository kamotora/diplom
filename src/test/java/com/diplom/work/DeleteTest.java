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
public class DeleteTest {
    @Autowired
    private RuleRepository ruleRepository;

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
    public void DeleteTest(){


        String oldClientNumber = "";

        /**
         * Тестим удаление записи
         */

        open("http://localhost:2345/");
        $(byName("username")).setValue("gerbert");
        $(byName("password")).setValue("12345").pressEnter();
        Selenide.sleep(3000);

        /**
         * Запоминаем прежние данные
         */
        Rule rule = ruleRepository.findAllByOrderByIdAsc().get(0);

        /**
         * Запоминаем прежний размер коллекции
         */

        int size = $$("#mytable tbody tr").size();
        $(byName("удал")).click();
        Selenide.sleep(3000);

        /**
         * Сравниваем, при удалении должен быть на 1 меньше
         */

        $$("#mytable tbody tr").shouldHaveSize(size-1);


        /**
         * Теперь вернём значения в базу
         */

        ruleRepository.save(rule);

    }
}
