package com.diplom.work.svc;

import com.diplom.work.core.Log;
import com.diplom.work.core.Rule;

import java.util.List;

public interface WorkApplicationService {
    Rule getOneRowById(Integer id);
    void saveOneRow(Rule rule);
    void updateOneRow(Integer id, String client, String number,String FIOClient);
    void deleteOneRow(Integer id);
    List<Rule> findAllByOrderByIdAsc();

    Log getOneLogById(Integer id);
    void saveOneLog(Log log);
    void updateOneLog(Integer id, String session_id, String type, String state, String from_number, String request_number);
    void deleteOneLog(Integer id);
    List<Log> findAllByOrderByTimestampAsc();

}
