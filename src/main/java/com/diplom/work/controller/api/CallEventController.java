package com.diplom.work.controller.api;

import com.diplom.work.core.Log;
import com.diplom.work.repo.LogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("api/")
public class CallEventController {
    private final LogRepository logRepository;
    @Autowired
    public CallEventController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CallEventController.class);

    @Value("${api.Client-ID}")
    private String myClientID;
    @Value("${api.Client-Key}")
    private String myClientKey;

    @PostMapping(path = "call_events",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> getNewCall(@RequestHeader(name = "X-Client-ID", value = "", required = false) String clientID,
                                           @RequestHeader(name = "X-Client-Sign" , value = "" , required = false)String clientSign,
                                           @RequestBody Log callEvent) {

        logRepository.save(callEvent);
        LOGGER.warn("Получили запрос на call_events, body = "+callEvent.toString());
        return ResponseEntity.ok().build();
    }


}
