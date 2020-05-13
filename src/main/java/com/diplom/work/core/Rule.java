package com.diplom.work.core;

import com.diplom.work.core.json.view.RuleViews;
import com.diplom.work.core.user.User;
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
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(RuleViews.onlyId.class)
    private Long id;

    //Наименование правила
    @Column(name = "name", nullable = false)
    @JsonView(RuleViews.forTable.class)
    private String name;

    //Номер менеджераMethod threw 'java.lang.StackOverflowError' exception.
    @Column(name = "manager_number", nullable = true)
    private String managerNumber;

    //Или учётная запись менеджера
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = true)
    private User manager;

    //Умная маршрутиазация
    @Column(name = "is_smart", nullable = false)
    private Boolean isSmart;

    // Для всех клиентов
    @Column(name = "is_for_all_clients", nullable = false)
    private Boolean isForAllClients;

    //Клиенты в правиле
    @ManyToMany(targetEntity = Client.class, fetch = FetchType.EAGER)
    @JoinTable(name="client_rule",
            joinColumns = @JoinColumn(name="rule_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name="client_id", referencedColumnName="id")
    )
    private Set<Client> clients;

    //Дни действия
    @ElementCollection(targetClass = Days.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "rule_days", joinColumns = @JoinColumn(name = "rule_id"))
    @Enumerated(EnumType.STRING)
    private Set<Days> days;

    @Transient
    private Time timeStart;
    @Transient
    private Time timeFinish;


    private String timeStartString;
    private String timeFinishString;

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
