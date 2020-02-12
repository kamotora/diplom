package com.diplom.work.svc;

import com.diplom.work.core.OneRow;
import com.diplom.work.repo.OneRowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkApplicationServiceImpl implements WorkApplicationService {

    private OneRowRepository oneRowRepository;

    @Autowired
    public void setOneRowRepository(OneRowRepository oneRowRepository){
        this.oneRowRepository = oneRowRepository;
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
}
