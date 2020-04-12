package com.diplom.work.svc;

import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void deleteUserByUsername(String username)  throws UsernameNotFoundException{
        User user = userRepo.findByUsername(username);
        if(user == null)
            throw new UsernameNotFoundException("Такой пользователь не найден");
        user.setRoles(new HashSet<>());
        userRepo.save(user);
        userRepo.delete(user);
    }

    public void save(User user){
        userRepo.save(user);
    }

    public User findByUsername(String username) throws UsernameNotFoundException {
        if(userRepo == null)
            System.out.println("kek");
        return userRepo.findByUsername(username);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(userRepo == null)
            System.out.println("kek");
        return userRepo.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void save(User user, Map<String, String> form, String username) {

        user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

    }
}