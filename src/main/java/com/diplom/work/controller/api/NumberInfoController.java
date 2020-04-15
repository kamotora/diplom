package com.diplom.work.controller.api;


import com.diplom.work.core.Rule;
import com.diplom.work.core.json.NumberInfo;
import com.diplom.work.core.json.NumberInfoAnswer;
import com.diplom.work.repo.RuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
public class NumberInfoController {
    private final RuleRepository ruleRepository;
    @Autowired
    public NumberInfoController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }
    @Value("${api.Client-ID}")
    private String myClientID;
    @Value("${api.Client-Key}")
    private String myClientKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberInfoController.class);

    @PostMapping(path = "get_number_info",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE}) //TODO required убрать после отладки
    public ResponseEntity<NumberInfoAnswer> getNewCall(@RequestHeader(name = "X-Client-ID", value = "", required = false) String clientID,
                                                       @RequestHeader(name = "X-Client-Sign" , value = "" , required = false)String clientSign,
                                                       @RequestBody NumberInfo numberInfo) {
        LOGGER.debug(String.format("Получили запрос на get_number_info, header.X-Client-ID = %s \n" +
                "header.X-Client-Sign = %s \n, body = %s",clientID,clientSign,numberInfo.toString()));
        if(Strings.isNullOrEmpty(clientID) || Strings.isNullOrEmpty(clientSign))
            LOGGER.error("Headers не получены. Проверь name");
        if(!clientID.equals(myClientID))
            LOGGER.error("ClientID не совпадают");
        try{
            Rule rule = ruleRepository.findByClientNumber(numberInfo.getFrom_number());
            if(rule == null)
                throw new IncorrectResultSizeDataAccessException(0);
            NumberInfoAnswer answer = new NumberInfoAnswer(rule.getManagerNumber(), rule.getClientNumber());
            HttpHeaders headers = Utils.getHeaders(answer, clientID, myClientKey);
            //TODO удалить после отладки
            LOGGER.warn(ResponseEntity.status(200).headers(headers).body(answer).toString());
            //
            return ResponseEntity.status(200).headers(headers).body(answer);
        }catch (IncorrectResultSizeDataAccessException e){
            return ResponseEntity.status(404).body(new NumberInfoAnswer(404, "Не найдено информации о том, куда перенаправлять"));
        }catch (JsonProcessingException e){
            e.printStackTrace();
            LOGGER.error("Не удалось получить JSON ответа");
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, "Не удалось получить JSON ответа"));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, "Неизвестная ошибка"));
        }
    }
}
