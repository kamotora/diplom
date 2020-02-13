package com.diplom.work.svc;

import com.diplom.work.core.OneLog;
import com.diplom.work.core.OneRow;
import com.diplom.work.repo.OneLogRepository;
import com.diplom.work.repo.OneRowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkApplicationServiceImpl implements WorkApplicationService {

    @Autowired
    private OneRowRepository oneRowRepository;
    @Autowired
    private OneLogRepository oneLogRepository;

    public WorkApplicationServiceImpl(OneRowRepository oneRowRepository, OneLogRepository oneLogRepository) {
        this.oneRowRepository = oneRowRepository;
        this.oneLogRepository = oneLogRepository;
    }

    @Override
    public OneRow getOneRowById(Integer id) {
        return oneRowRepository.getOne(id);
    }

    @Override
    public void saveOneRow(OneRow oneRow) {
        oneRowRepository.save(oneRow);
    }

    @Override
    public void updateOneRow(Integer id, String client, String number, String FIOClient) {
        OneRow update = oneRowRepository.getOne(id);
        update.setClient(client);
        update.setFIOClient(FIOClient);
        update.setNumber(number);
        oneRowRepository.save(update);
    }

    @Override
    public void deleteOneRow(Integer id) {
        oneRowRepository.deleteById(id);
    }

    @Override
    public List<OneRow> findAllByOrderByClientAsc() {
        return oneRowRepository.findAllByOrderByClientAsc();
    }

    @Override
    public OneLog getOneLogById(Integer id) {
        return oneLogRepository.getOne(id);
    }

    @Override
    public void saveOneLog(OneLog oneLog) {
        oneLogRepository.save(oneLog);
    }

    @Override
    public void updateOneLog(Integer id, String session_id, String type, String state, String from_number, String request_number) {
        OneLog update = oneLogRepository.getOne(id);
        update.setFrom_number(from_number);
        update.setRequest_number(request_number);
        update.setSession_id(session_id);
        update.setState(state);
        update.setType(type);
        oneLogRepository.save(update);
    }

    @Override
    public void deleteOneLog(Integer id) {
        oneLogRepository.deleteById(id);
    }

    @Override
    public List<OneLog> findAllByOrderByTimestampAsc() {
        return oneLogRepository.findAllByOrderByTimestampInDateTimeFormatAsc();
    }
}
