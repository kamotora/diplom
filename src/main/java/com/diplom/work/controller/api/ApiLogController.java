package com.diplom.work.controller.api;

import com.diplom.work.core.Log;
import com.diplom.work.core.Rule;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.core.user.User;
import com.diplom.work.svc.LogService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(path = "/dataGraphic", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Number> getDataForGraphic() {
        List<Number> listDataForGraphic = new ArrayList<>();

        int numerusIncoming = 0;
        int numerusOutbound = 0;
        int numerusInternal = 0;

        List<Log> logs = logService.findAllByOrderByTimestampAsc();

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
