package com.diplom.work.svc;

import com.diplom.work.controller.api.exceptions.NewPasswordsNotEquals;
import com.diplom.work.controller.api.exceptions.UsernameAlreadyExist;
import com.diplom.work.core.dto.UserEditDto;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.repo.UserRepository;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.function.SupplierUtils;

import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean deleteUserById(Long id) throws UsernameNotFoundException {
        if(id == null || id == 0)
            throw new UsernameNotFoundException("Такой пользователь не найден");
        User user = userRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException("Такой пользователь не найден"));
        if (user == null)
            throw new UsernameNotFoundException("Такой пользователь не найден");
        user.setRoles(new HashSet<>());
        userRepo.save(user);
        userRepo.delete(user);
        return true;
    }

    public void save(User user) {
        userRepo.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User save(UserEditDto user) throws NewPasswordsNotEquals, UsernameAlreadyExist {
        if (StringUtils.isBlank(user.getPassword1()) || StringUtils.isBlank(user.getPassword2()) || !user.getPassword1().equals(user.getPassword2())) {
            throw new NewPasswordsNotEquals();
        }
        if ((user.getId() == null || user.getId() == 0) && userRepo.findByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyExist();
        }
        User result = findByIdOrCreateNewUser(user.getId());
        BeanUtils.copyProperties(user, result, "id");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(user.getRole());
        result.setRoles(roleSet);
        result.setPassword(passwordEncoder.encode(user.getPassword1()));
        save(result);
        return result;
    }

    public User findByIdOrCreateNewUser(Long id) {
        if(id == null)
            return new User();
        return userRepo.findById(id).orElse(new User());
    }

}