package com.diplom.work.svc;

import com.diplom.work.core.OneRow;

import java.util.List;

public interface WorkApplicationService {
    OneRow getOneRowById(Integer id);
    void saveOneRow(OneRow oneRow);
    void updateOneRow(Integer id, String client, String number, String FIOClient);
    void deleteOneRow(Integer id);
    List<OneRow> findAllByOrderByClientAsc();
}
