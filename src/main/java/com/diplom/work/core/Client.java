package com.diplom.work.core;

import com.diplom.work.core.json.view.ClientViews;
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
    @JsonView(ClientViews.onlyId.class)
    private Long id;
    @JsonView(ClientViews.forTable.class)
    private String name;
    @JsonView(ClientViews.forTable.class)
    @Column(nullable = false)
    private String number;
    @Column(name = "last_manager_number", nullable = true)
    private String lastManagerNumber;

    @ManyToMany(targetEntity = Rule.class, mappedBy = "clients", fetch = FetchType.EAGER)
    private Set<Rule> rules;

    public Client(String number) {
        this.number = number;
    }
}
