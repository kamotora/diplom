package com.diplom.work.repo;

import com.diplom.work.core.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RuleRepository extends JpaRepository<Rule,Long> {
    List<Rule> findAllByOrderByIdAsc();
    Set<Rule> findAllByIsForAllClientsIsTrue();
}
