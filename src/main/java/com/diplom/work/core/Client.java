package com.diplom.work.core;

import com.diplom.work.core.json.view.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@ToString(of = {"id", "name", "number", "lastManagerNumber"})
@EqualsAndHashCode(of = {"id", "name", "number", "lastManagerNumber"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.OnlyId.class)
    private Long id;

    /**
     * ФИО клиента
     */
    @JsonView(Views.ForTable.class)
    @Column(length = 1024)
    private String name;

    /**
     * Номер телефона
     */
    @JsonView(Views.ForTable.class)
    @Column(nullable = false)
    private String number;

    /**
     * Последний номер в виде PIN, с которым был разговор у клиента
     */
    @Column(name = "last_manager_number", nullable = true)
    @JsonView(Views.SimpleObject.class)
    private String lastManagerNumber;

    /**
     * Список правил, где участвует данный клиент
     */
    @ManyToMany(targetEntity = Rule.class, mappedBy = "clients", fetch = FetchType.EAGER)
    @JsonView(Views.AllClient.class)
    private Set<Rule> rules = new HashSet<>();

    public Client(String number) {
        this.number = number;
    }
}
