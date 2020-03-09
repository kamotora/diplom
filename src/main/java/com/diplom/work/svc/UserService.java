package com.diplom.work.svc;

import com.diplom.work.core.user.User;
import com.diplom.work.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    public void deleteUserByUsername(String username)  throws UsernameNotFoundException{
        User user = userRepo.findByUsername(username);
        if(user == null)
            throw new UsernameNotFoundException("Такой пользователь не найден");
        user.setRoles(new HashSet<>());
        userRepo.save(user);
        userRepo.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(userRepo == null)
            System.out.println("kek");
        return userRepo.findByUsername(username);
    }
}