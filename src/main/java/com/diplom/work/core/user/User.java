package com.diplom.work.core.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "usr")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private boolean active;
    @Transient
    public static final BCryptPasswordEncoder TYPE_ENCRYPT = new BCryptPasswordEncoder();
    public User() {
    }


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    /**
     * @return Возвращает права, предоставленные пользователю.
     * */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
    /**
     * Указывает, истек ли срок действия учетной записи пользователя. Учетная запись с истекшим сроком не может быть аутентифицирована.
     * @return
     * true если учетная запись пользователя действительна (то есть, не истек)
     * false если больше не действительна (т.е. истек)
     * */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, истек ли срок действия учетной записи пользователя.
     * @return true если пользователь не заблокирован, false противном случае
     * */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, истек ли срок действия учетных данных пользователя (пароля).
     * четные данные с истекшим сроком действия предотвращают аутентификацию.
     * @return
     * true если учетные данные пользователя действительны (то есть не истек),
     * false если больше не действительны (то есть истек)
     * */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * Указывает, включен ли пользователь или отключен. Отключенный пользователь не может быть аутентифицирован.
     * @return
     * true если пользователь включен
     * false в противном случае
     * */
    @Override
    public boolean isEnabled() {
        return isActive();
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPasswordAndEncrypt(String password){
        setPassword(User.TYPE_ENCRYPT.encode(password));
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
