package com.diplom.work.svc;

import com.diplom.work.exceptions.NewPasswordsNotEquals;
import com.diplom.work.exceptions.OldPasswordsNotEquals;
import com.diplom.work.exceptions.UsernameAlreadyExist;
import com.diplom.work.core.dto.UserEditDto;
import com.diplom.work.core.user.Role;
import com.diplom.work.core.user.User;
import com.diplom.work.repo.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;

        //Если пользователей вообще нет, создадим админа по-умолчанию
        if(userRepo.count() == 0){
            final User defaultAdmin = new User();
            defaultAdmin.setName("admin");
            defaultAdmin.setPassword(passwordEncoder.encode("admin"));
            defaultAdmin.getRoles().add(Role.ADMIN);
            defaultAdmin.setActive(true);
            userRepo.save(defaultAdmin);
            System.out.println("Был создан администратор по умолчанию:\n Логин:admin\nПароль:admin");
        }
    }

    public boolean deleteUserById(Long id) throws UsernameNotFoundException {
        if (id == null || id == 0)
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

    public List<User> findAllByRole(Role role){
        return userRepo.findAllByRolesContaining(role);
    }

    public User save(UserEditDto user) throws NewPasswordsNotEquals, UsernameAlreadyExist {
        User result = findByIdOrCreateNewUser(user.getId());
        boolean isNewUser = (user.getId() == null || user.getId() == 0);
        boolean needSetPassword = false;

        //Если новые пароли пустые
        if (StringUtils.isEmptyOrWhitespace(user.getPassword1()) || StringUtils.isEmptyOrWhitespace(user.getPassword2())) {
            //Если это новый пользователь, пароль обязателен
            if (isNewUser) {
                throw new NewPasswordsNotEquals();
            }
            //Если это существующий и пароли пустые - значит и менять их не надо
            //Если пароли не пустые, проверяем на одинаковость в любом случае
        } else if (!user.getPassword1().equals(user.getPassword2()))
            throw new NewPasswordsNotEquals();
        else
            //Если пароли не пустые и одинаковые - надо менять
            needSetPassword = true;
        if (isNewUser && userRepo.findByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyExist();
        }

        //Копируем все поля кроме id
        BeanUtils.copyProperties(user, result, "id");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(user.getRole());
        if(needSetPassword)
            result.setPassword(passwordEncoder.encode(user.getPassword1()));
        result.setRoles(roleSet);
        save(result);
        return result;
    }

    public User findByIdOrCreateNewUser(Long id) {
        if (id == null)
            return new User();
        return userRepo.findById(id).orElse(new User());
    }

    public boolean changePassword(User oldUser, UserEditDto newUser) throws NewPasswordsNotEquals, OldPasswordsNotEquals {
        if (StringUtils.isEmptyOrWhitespace(newUser.getPassword1()) || StringUtils.isEmptyOrWhitespace(newUser.getPassword2()) || !newUser.getPassword1().equals(newUser.getPassword2())) {
            throw new NewPasswordsNotEquals();
        }

        if (!passwordEncoder.matches(newUser.getOldPassword(), oldUser.getPassword())) {
            throw new OldPasswordsNotEquals();
        }

        oldUser.setPassword(passwordEncoder.encode(newUser.getPassword1()));
        save(oldUser);
        return true;
    }
}