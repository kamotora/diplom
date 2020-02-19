package com.diplom.work.controller.api;

import com.diplom.work.core.OneLog;
import com.diplom.work.core.OneRow;
import com.diplom.work.repo.OneRowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Контроллер правил марштуризации
 * */
public class RuleController {
    @Autowired
    private OneRowRepository oneRowRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleController.class);

    /**
     * Добавить новое правило марштуризации
     * Тело запроса ожидается в виде JSON
     *
     * */
    @PostMapping(path = "api/add_rule",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> addRule(@RequestBody OneRow rule) {
        oneRowRepository.save(rule);
        LOGGER.warn("Получили запрос на добавление правила add_rule, body = "+rule.toString());
        return ResponseEntity.ok().build();
    }
}
