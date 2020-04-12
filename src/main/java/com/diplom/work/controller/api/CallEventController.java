package com.diplom.work.controller.api;

import com.diplom.work.core.Log;
import com.diplom.work.repo.LogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
public class CallEventController {
    private final LogRepository logRepository;
    @Autowired
    public CallEventController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CallEventController.class);

    @PostMapping(path = "call_events",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> getNewCall(@RequestBody Log callEvent) {
        logRepository.save(callEvent);
        LOGGER.warn("Получили запрос на call_events, body = "+callEvent.toString());
        return ResponseEntity.ok().build();
    }


}
