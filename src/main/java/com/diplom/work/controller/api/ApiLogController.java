package com.diplom.work.controller.api;

import com.diplom.work.core.Log;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.core.user.User;
import com.diplom.work.svc.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("rest/logs")
@Slf4j
public class ApiLogController {

    private final LogService logService;

    @Autowired
    public ApiLogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping(path = "/updateDataForGraphics", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Number> getUpdateDataForGraphic(@RequestBody(required = false) LogFilterDto logFilterDto, @AuthenticationPrincipal User user) {
        List<Log> logs = logService.findAll(user, logFilterDto);
        return getNumerusList(logs);
    }


    public List<Number> getNumerusList(List<Log> logs) {

        List<Number> listDataForGraphic = new ArrayList<>();
        int numerusIncoming = 0;
        int numerusOutbound = 0;
        int numerusInternal = 0;

        for (Log curLog : logs) {
            switch (curLog.getType()) {
                case "incoming":
                    numerusIncoming++;
                    break;
                case "outbound":
                    numerusOutbound++;
                    break;
                case "internal":
                    numerusInternal++;
                    break;
                default:
                    log.error("Неизвестный тип лога");
            }
        }
        listDataForGraphic.add(numerusIncoming);
        listDataForGraphic.add(numerusOutbound);
        listDataForGraphic.add(numerusInternal);
        return listDataForGraphic;

    }
}
