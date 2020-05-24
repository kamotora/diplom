package com.diplom.work.controller.api;

import com.diplom.work.core.Log;
import com.diplom.work.core.Rule;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.core.user.User;
import com.diplom.work.svc.LogService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/logs")
public class ApiLogController {

    private final LogService logService;

    @Autowired
    public ApiLogController(LogService logService){
        this.logService = logService;
    }


    /*
    * Возврат значений для графика
    *
    */
    @GetMapping(path = "/dataGraphic", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Number> getDataForGraphic() {
        List<Log> logs = logService.findAllByOrderByTimestampAsc();
        return getNumerusList(logs);
    }

    @PostMapping(path = "/updateDataForGraphics" , consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Number> getUpdateDataForGraphic(@RequestBody LogFilterDto logFilterDto){
        List<Log> logs = logService.findAllByFilter(logFilterDto);
        return getNumerusList(logs);
    }


    public List<Number> getNumerusList(List<Log> logs){

        List<Number> listDataForGraphic = new ArrayList<>();
        int numerusIncoming = 0;
        int numerusOutbound = 0;
        int numerusInternal = 0;

        for(int i = 0; i < logs.size(); i++){
            if(logs.get(i).getType().equals("incoming"))
                numerusIncoming++;
            else if (logs.get(i).getType().equals("outbound"))
                numerusOutbound++;
            else if (logs.get(i).getType().equals("internal"))
                numerusInternal++;
        }
        listDataForGraphic.add(numerusIncoming);
        listDataForGraphic.add(numerusOutbound);
        listDataForGraphic.add(numerusInternal);
        return listDataForGraphic;

    }
}
