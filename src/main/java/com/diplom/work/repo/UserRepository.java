package com.diplom.work.repo;

import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAllByRolesContaining(Role role);
}
