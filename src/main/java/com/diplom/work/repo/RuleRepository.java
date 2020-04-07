package com.diplom.work.repo;

import com.diplom.work.core.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleRepository extends JpaRepository<Rule,Integer> {
    List<Rule> findAllByOrderByClientNameAsc();
    Rule findByClientNumber(String clientNumber);
    List<Rule> findAllByOrderByIdAsc();
}
