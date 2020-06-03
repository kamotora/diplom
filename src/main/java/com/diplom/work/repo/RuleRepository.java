package com.diplom.work.repo;

import com.diplom.work.core.Rule;
import com.diplom.work.core.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RuleRepository extends JpaRepository<Rule,Long> {
    List<Rule> findAllByOrderByIdAsc();
    Set<Rule> findAllByIsForAllClientsIsTrue();
    //Список правил для сотрудника
    List<Rule> findAllByManager(User user);
    //Список правил для сотрудника + умные правила
    List<Rule> findAllByManagerOrIsSmartTrue(User user);
}
