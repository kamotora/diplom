package com.diplom.work.api;

import com.diplom.work.core.json.CallEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class CallEventController {
    @GetMapping(path = "call_events",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> getNewCall(@RequestBody CallEvent callEvent) {
        /*
        * Можно как-то обработать запрос
        * */
        return ResponseEntity.ok().build();
    }


}
