package com.diplom.work.api;


import com.diplom.work.core.OneRow;
import com.diplom.work.core.json.NumberInfo;
import com.diplom.work.core.json.NumberInfoAnswer;
import com.diplom.work.repo.OneRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class NumberInfoController {
    @Autowired
    private OneRowRepository oneRowRepository;
    @GetMapping(path = "get_number_info",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<NumberInfoAnswer> getNewCall(@RequestBody NumberInfo numberInfo) {
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
