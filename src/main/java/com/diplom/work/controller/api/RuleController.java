package com.diplom.work.controller.api;

import com.diplom.work.core.Rule;
import com.diplom.work.exceptions.BadRequestException;
import com.diplom.work.repo.RuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

/**
 * Контроллер правил марштуризации
 */
@RestController
@RequestMapping("api/")
public class RuleController {
    private final RuleRepository ruleRepository;

    @Autowired
    public RuleController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleController.class);

    /**
     * Добавить новое правило марштуризации
     * Тело запроса ожидается в виде JSON
     */
    @PostMapping(path = "add_rule",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> addRule(@RequestBody Rule rule) {
        LOGGER.warn("Получили запрос на добавление правила add_rule, body = " + rule.toString());
        try {
//            if (StringUtils.isEmptyOrWhitespace((rule.getClientNumber())))
//                throw new BadRequestException("ClientNumber == null or empty");
//            if (StringUtils.isEmptyOrWhitespace(rule.getManagerNumber()))
//                throw new BadRequestException("ManagerNumber == null or empty");
//            if (StringUtils.isEmptyOrWhitespace(rule.getClientName()))
//                throw new BadRequestException("ClientName == null or empty");
            ruleRepository.save(rule);
//        } catch (BadRequestException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Не удалось обработать запрос. Error:" + e.getMessage());
        }
        LOGGER.warn("Успешно обработали запрос на добавление правила add_rule, body = " + rule.toString());
        return ResponseEntity.ok().build();
    }
}
