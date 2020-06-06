package com.diplom.work.core;

import com.diplom.work.core.json.view.Views;
import com.diplom.work.core.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.util.StringUtils;

import javax.persistence.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Правило марштутизации вызова
 */

@Entity
@Table(name = "rule")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = {"id", "name", "isSmart", "isForAllClients"})
@ToString
@Slf4j
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.onlyId.class)
    private Long id;

    /**
     * Наименование правила
     */
    @Column(name = "name", nullable = false)
    @JsonView(Views.forTable.class)
    private String name;

    /**
     * Номер менеджера (null, если указан <code>manager</code>)
     */
    @Column(name = "manager_number", nullable = true)
    @JsonView(Views.simpleObject.class)
    private String managerNumber;

    /**
     * Учётная запись менеджера (null, если указан  <code>managerNumber</code> )
     */
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = true)
    @JsonView(Views.allRule.class)
    private User manager;

    /**
     * True, если активирнована умная маршрутиазация
     */
    @Column(name = "is_smart", nullable = false)
    @JsonView(Views.simpleObject.class)
    private Boolean isSmart;

    /**
     * True,если правило работает для всех клиентов
     */
    @Column(name = "is_for_all_clients", nullable = false)
    @JsonView(Views.simpleObject.class)
    private Boolean isForAllClients;

    /**
     * Список клиентов для данного правила
     *
     * @see Client
     */
    @ManyToMany(targetEntity = Client.class, fetch = FetchType.EAGER)
    @JoinTable(name = "client_rule",
            joinColumns = @JoinColumn(name = "rule_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id")
    )
    @JsonView(Views.allRule.class)
    private Set<Client> clients = new HashSet<>();

    /**
     * Дни действия правила
     */
    @ElementCollection(targetClass = Days.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "rule_days", joinColumns = @JoinColumn(name = "rule_id"))
    @Enumerated(EnumType.STRING)
    private Set<Days> days = new HashSet<>();

    //Приоритет
    @Column(name = "priority", nullable = true)
    @JsonView(Views.forTable.class)
    private Integer priority;

    /**
     * Время начала действия правила
     * Используется при расчёте, можно ли в данный момент использовать данное правило
     */
    @Transient
    private Time timeStart;

    /**
     * Время окончания действия правила
     * Используется при расчёте, можно ли в данный момент использовать данное правило
     */
    @Transient
    private Time timeFinish;

    /**
     * Время начала действия правила в виде строки
     * Используется для ввода с формы и вывода на форму
     */
    @JsonView(Views.simpleObject.class)
    private String timeStartString;


    /**
     * Время окончания действия правила в виде строки
     * Используется для ввода с формы и вывода на форму
     */
    @JsonView(Views.simpleObject.class)
    private String timeFinishString;

    /**
     * Конвертирует <code>timeStartString</code> в объект класса Time
     *
     * @return время начала
     */
    public Time getTimeStart() {
        if (timeStart == null)
            timeStart = getTimeFromString(timeStartString);
        return timeStart;
    }


    /**
     * Конвертирует <code>timeFinishString</code> в объект класса Time
     *
     * @return время окончания
     */
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
                log.error("Исключение в методе Rule.getTimeFromString",e);
            }
        }
        return null;
    }
}
