package com.diplom.work.controller;

import com.diplom.work.core.Log;
import com.diplom.work.core.dto.CallInfo;
import com.diplom.work.core.dto.GetRecord;
import com.diplom.work.core.dto.LogFilterDto;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.svc.CallService;
import com.diplom.work.svc.LogService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@Slf4j
public class LogsController {
    private final LogService logService;
    private final CallService callService;

    @Autowired
    public LogsController(LogService logService, CallService callService) {
        this.logService = logService;
        this.callService = callService;
    }

    /**
     * Вывод страницы с таблицей
     */
    @GetMapping(path = "/logs")
    public String listLogs(Model model) {
        return "logs";
    }

    /**
     * Возврат всех логов для таблицы в виде JSON (таблица на JS)
     *
     * @return всех логов в виде JSON
     */
    @GetMapping(path = "/logs", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.forTable.class)
    public ResponseEntity<List<Log>> getLogsForTable() {
        List<Log> ok = logService.findAllByOrderByTimestampAsc();
        return ResponseEntity.ok(ok);
    }

    /**
     * Возврат логов для таблицы в виде JSON (таблица на JS) ПО ФИЛЬТРУ
     *
     * @return всех логов в виде JSON
     */
    @PostMapping(path = "/logs", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.forTable.class)
    public ResponseEntity<List<Log>> getLogsForTableByFilter(@RequestBody LogFilterDto logFilterDto) {
        List<Log> logs = logService.findAllByFilter(logFilterDto);
        return ResponseEntity.ok(logs);
    }

    /**
     * Удаление логов по массиву IDs
     *
     * @param ids - массив с ID логов
     * @return блок с сообщениями об успехе/ошибке для его вывода через jquery
     */
    @DeleteMapping("log")
    public String deleteLog(Model model, @RequestBody List<Long> ids) {
        try {
            ids.forEach(logService::deleteOneLog);
            model.addAttribute("goodMessage", "Удалено");
        } catch (Exception exception) {
            log.error("Ошибка при удалении лога: {}", exception.getMessage());
            model.addAttribute("badMessage", "Возникла ошибка при удалении");
        }
        return "fragments/messages :: messages";
    }


    @GetMapping("/log/{id}/view")
    public String showCallInfo(@PathVariable("id") Log log, Model model,HttpServletRequest request){
        CallInfo errorInfo = new CallInfo(-1000, "Не удалось получить инфу", null);
        try{
            /**
             * Это для отладки
             */

            CallInfo callInfo = new CallInfo(0,"Основная информация по вызову", new CallInfo.Info(1,2,1,"89545342312","210","123212321",null,null,"10.10.2020",5,"краткий лог",false,true,true,"textForError","statusError"));
            GetRecord record = new GetRecord("0","Запись разговора","https://api.cloudpbx.rt.ru/records_new_scheme/record/download/9c8518024e2af41bf1056773219997d6/188254033196");
            model.addAttribute("nameSession", log.getSession_id());
            model.addAttribute("callInfo", callInfo);
            model.addAttribute("record", record);

            /**
             * Эт если с сервака
             */
            /*
            String ipClient = request.getHeader("X-Forwarded-For");
            CallInfo callInfo = callService.getCallInfoBySessionID(log.getSession_id());
            GetRecord record = callService.getRecordBySessionID(log.getSession_id(),ipClient);
            model.addAttribute("nameSession", log.getSession_id());
            model.addAttribute("callInfo", callInfo);
            model.addAttribute("record", record);*/
            return "log";
        }catch(Exception e){
            model.addAttribute("callInfo", errorInfo);
            return "log";
        }
    }

    /**
     * Получаем от ВАТС инфу о вызове
     * и выводим её
     */
    /*
    @GetMapping("log/{id}/view")
    public ResponseEntity<CallInfo> showCallInfo(@PathVariable("id") Log log) {
        CallInfo errorInfo = new CallInfo(-1000,"Не удалось получить инфу",null);
        try {
            return ResponseEntity.ok(callService.getCallInfoBySessionID(log.getSession_id()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(errorInfo);
        }
    }
*/
    //TECT

    /**
     * Получаем от ВАТС запись звонка
     * но пока нихуя не работает ибо метод отлключен и хз как отлаживать
     */
    @GetMapping("log/{id}/record")
    public ResponseEntity<GetRecord> showCallRecord(@PathVariable("id") Log log, Model model, HttpServletRequest request) {
        GetRecord errorRecord = new GetRecord("-1000","Не удалось получить запись","");
        String ipClient = request.getHeader("X-Forwarded-For");
        try {
            GetRecord record = callService.getRecordBySessionID(log.getSession_id(), ipClient);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(errorRecord);
        }
    }

}
