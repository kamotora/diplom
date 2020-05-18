package com.diplom.work.core;

import com.diplom.work.core.json.view.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@ToString(of = {"id", "name", "number", "lastManagerNumber"})
@EqualsAndHashCode(of = {"id", "name", "number", "lastManagerNumber"})
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.onlyId.class)
    private Long id;
    @JsonView(Views.forTable.class)
    private String name;
    @JsonView(Views.forTable.class)
    @Column(nullable = false)
    private String number;
    @Column(name = "last_manager_number", nullable = true)
    @JsonView(Views.simpleObject.class)
    private String lastManagerNumber;

    @ManyToMany(targetEntity = Rule.class, mappedBy = "clients", fetch = FetchType.EAGER)
    @JsonView(Views.allClient.class)
    private Set<Rule> rules;

    public Client(String number) {
        this.number = number;
    }
}
