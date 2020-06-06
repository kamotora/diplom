package com.diplom.work.core.user;

import com.diplom.work.core.json.view.Views;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usr")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id","username","password"})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.onlyId.class)
    private Long id;
    @JsonView(Views.forTable.class)
    @Column(length = 1024, nullable = false)
    private String username;
    @JsonView(Views.simpleObject.class)
    @Column(length = 1024, nullable = false)
    private String password;
    @JsonView(Views.forTable.class)
    @Column(length = 2048)
    private String name;
    @JsonView(Views.forTable.class)
    private String number;
    @JsonView(Views.simpleObject.class)
    private String email;
    @JsonView(Views.simpleObject.class)
    private String token;
    @JsonView(Views.simpleObject.class)
    private boolean active;
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @JsonView(Views.allUser.class)
    private Set<Role> roles = new HashSet<>();

    /**
     * @return Возвращает права, предоставленные пользователю.
     */
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
     *
     * @return true если учетная запись пользователя действительна (то есть, не истек)
     * false если больше не действительна (т.е. истек)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, истек ли срок действия учетной записи пользователя.
     *
     * @return true если пользователь не заблокирован, false противном случае
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, истек ли срок действия учетных данных пользователя (пароля).
     * четные данные с истекшим сроком действия предотвращают аутентификацию.
     *
     * @return true если учетные данные пользователя действительны (то есть не истек),
     * false если больше не действительны (то есть истек)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, включен ли пользователь или отключен. Отключенный пользователь не может быть аутентифицирован.
     *
     * @return true если пользователь включен
     * false в противном случае
     */
    @Override
    public boolean isEnabled() {
        return isActive();
    }

    /**
     * В приложении можно выбрать одну роль
     * Спринг допускает несколько
     * Поэтому костыль
     * @return первую роль из списка или null, если ролей не указано
     * */
    @JsonView(Views.forTable.class)
    @JsonGetter("role")
    public String getFirstRoleName() {
        if(roles.isEmpty())
            return null;
        return getFirstRole().getAuthority();
    }
    @Transient
    public Role getFirstRole() {
        return roles.iterator().next();
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
