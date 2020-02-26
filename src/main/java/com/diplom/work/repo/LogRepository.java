package com.diplom.work.repo;

import com.diplom.work.core.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log,Integer> {
    List<Log> findAllByOrderByTimestampInDateTimeFormatAsc();
}
