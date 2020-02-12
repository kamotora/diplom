package com.diplom.work.repo;

import com.diplom.work.core.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Route findByFrom(String from);
}
