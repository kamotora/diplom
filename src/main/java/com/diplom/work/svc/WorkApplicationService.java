package com.diplom.work.svc;

import com.diplom.work.core.OneLog;
import com.diplom.work.core.OneRow;

import java.util.List;
import java.util.Optional;

public interface WorkApplicationService {
    OneRow getOneRowById(Integer id);
    void saveOneRow(OneRow oneRow);
    void updateOneRow(Integer id, String client, String number,String FIOClient);
    void deleteOneRow(Integer id);
    List<OneRow> findAllByOrderByClientAsc();

    OneLog getOneLogById(Integer id);
    void saveOneLog(OneLog oneLog);
    void updateOneLog(Integer id, String session_id, String type, String state, String from_number, String request_number);
    void deleteOneLog(Integer id);
    List<OneLog> findAllByOrderByTimestampAsc();

}
