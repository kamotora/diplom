package com.diplom.work.api;

import com.diplom.work.core.OneLog;
import com.diplom.work.repo.OneLogRepository;
import com.diplom.work.svc.WorkApplicationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class CallEventController {
    @Autowired
    private OneLogRepository oneLogRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CallEventController.class);


    @PostMapping(path = "call_events",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> getNewCall(@RequestBody OneLog callEvent) {
        oneLogRepository.save(callEvent);
        LOGGER.warn("Получили запрос на call_events, body = "+callEvent.toString());
        return ResponseEntity.ok().build();
    }


}
