package com.diplom.work.repo;

import com.diplom.work.core.OneLog;
import com.diplom.work.core.OneRow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OneLogRepository extends JpaRepository<OneLog,Integer> {
    List<OneLog> findAllByOrderBySessionAsc();
}
