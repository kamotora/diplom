package com.diplom.work.api;


import com.diplom.work.core.OneRow;
import com.diplom.work.core.json.NumberInfo;
import com.diplom.work.core.json.NumberInfoAnswer;
import com.diplom.work.repo.OneRowRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class NumberInfoController {
    @Autowired
    private OneRowRepository oneRowRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberInfoController.class);

    @PostMapping(path = "get_number_info",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<NumberInfoAnswer> getNewCall(@RequestBody NumberInfo numberInfo) {
        LOGGER.debug("Получили запрос на get_number_info, body = "+numberInfo.toString());
        try{
            OneRow oneRow = oneRowRepository.findByClient(numberInfo.getFrom_number());
            return ResponseEntity.ok(new NumberInfoAnswer(oneRow.getNumber(), oneRow.getClient()));
        }catch (IncorrectResultSizeDataAccessException e){
            return ResponseEntity.status(404).body(new NumberInfoAnswer(404, "Не найдено информации о том, куда перенаправлять"));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, "Неизвестная ошибка"));
        }
    }
}
