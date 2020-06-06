package com.diplom.work.repo;

import com.diplom.work.core.Client;
import com.diplom.work.core.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Set<Client> findAllByRulesContaining(Rule rule);

    Client findFirstByNumber(String number);

    Client findFirstByNumberContaining(String number);
}
