package com.diplom.work.api;

import com.diplom.work.core.json.CallEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class CallEventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallEventController.class);


    @PostMapping(path = "call_events",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> getNewCall(@RequestBody CallEvent callEvent) {
        /*
        * Можно как-то обработать запрос
        * */
        LOGGER.debug("Получили запрос на call_events, body = "+callEvent.toString());
        return ResponseEntity.ok().build();
    }


}
