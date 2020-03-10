package com.diplom.work;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.diplom.work.core.Rule;
import com.diplom.work.repo.RuleRepository;
import com.diplom.work.svc.WorkApplicationService;
import org.junit.Assert;
import org.junit.Test;


import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

@SpringBootTest
public class DeleteTest {
    @Autowired
    private RuleRepository ruleRepository;

    @Test
    public void DeleteTest(){
        /**
         *
         * Тестим удаление записи
         */

        open("http://localhost:2345/");
        $(byName("username")).setValue("gerbert");
        $(byName("password")).setValue("12345").pressEnter();
        Selenide.sleep(3000);

        /**
         *
         * Получаем исходный размер коллекции
         */

        int size = $$("#mytable tbody tr").size();
        $(byName("удал")).click();
        Selenide.sleep(3000);

        /**
         *
         * Сравниваем, при удалении должен быть на 1 меньше
         */

        $$("#mytable tbody tr").shouldHaveSize(size-1);
    }
}
