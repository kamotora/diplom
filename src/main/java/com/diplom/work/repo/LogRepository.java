package com.diplom.work.repo;

import com.diplom.work.core.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface LogRepository extends JpaRepository<Log,Long> {
    Set<Log> findAllByState(String state);
}
