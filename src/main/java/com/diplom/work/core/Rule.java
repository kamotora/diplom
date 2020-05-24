package com.diplom.work.core;

import com.diplom.work.core.json.view.Views;
import com.diplom.work.core.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.ToString;
import org.thymeleaf.util.StringUtils;

import javax.persistence.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Set;

/**
 * Правило марштутизации вызова
 */

@Entity
@Table(name = "rule")
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.onlyId.class)
    private Long id;

    //Наименование правила
    @Column(name = "name", nullable = false)
    @JsonView(Views.forTable.class)
    private String name;

    //Номер менеджера
    @Column(name = "manager_number", nullable = true)
    @JsonView(Views.simpleObject.class)
    private String managerNumber;

    //Или учётная запись менеджера
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = true)
    @JsonView(Views.allRule.class)
    private User manager;

    //Умная маршрутиазация
    @Column(name = "is_smart", nullable = false)
    @JsonView(Views.simpleObject.class)
    private Boolean isSmart;

    // Для всех клиентов
    @Column(name = "is_for_all_clients", nullable = false)
    @JsonView(Views.simpleObject.class)
    private Boolean isForAllClients;

    //Клиенты в правиле
    @ManyToMany(targetEntity = Client.class, fetch = FetchType.EAGER)
    @JoinTable(name = "client_rule",
            joinColumns = @JoinColumn(name = "rule_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id")
    )
    @JsonView(Views.allRule.class)
    private Set<Client> clients;

    //Дни действия
    @ElementCollection(targetClass = Days.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "rule_days", joinColumns = @JoinColumn(name = "rule_id"))
    @Enumerated(EnumType.STRING)
    private Set<Days> days;

    //Приоритет
    @Column(name = "priority", nullable = true)
    @JsonView(Views.forTable.class)
    private Integer priority;

    @Transient
    private Time timeStart;
    @Transient
    private Time timeFinish;

    @JsonView(Views.simpleObject.class)
    private String timeStartString;
    @JsonView(Views.simpleObject.class)
    private String timeFinishString;

    public Rule() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(id, rule.id) &&
                Objects.equals(name, rule.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }


    public Time getTimeStart() {
        if (timeStart == null)
            timeStart = getTimeFromString(timeStartString);
        return timeStart;
    }

    public Time getTimeFinish() {
        if (timeFinish == null)
            timeFinish = getTimeFromString(timeFinishString);
        return timeFinish;
    }

    private Time getTimeFromString(String string) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        if (!StringUtils.isEmptyOrWhitespace(string)) {
            try {
                return new Time(dateFormat.parse(string).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
