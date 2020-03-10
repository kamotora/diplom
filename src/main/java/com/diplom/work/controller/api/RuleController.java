package com.diplom.work.controller.api;

import com.diplom.work.core.Rule;
import com.diplom.work.repo.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер правил марштуризации
 * */
@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class RuleController {

    private RuleRepository ruleRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleController.class);

    /**
     * Добавить новое правило марштуризации
     * Тело запроса ожидается в виде JSON
     *
     * */
    @PostMapping(path = "add_rule",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> addRule(@RequestBody Rule rule) {
        try {
            ruleRepository.save(rule);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Не удалось обработать запрос");
        }
        LOGGER.warn("Получили запрос на добавление правила add_rule, body = "+rule.toString());
        return ResponseEntity.ok().build();
    }
}
