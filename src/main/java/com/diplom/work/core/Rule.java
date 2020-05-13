package com.diplom.work.core;

import com.diplom.work.core.json.view.LogsViews;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Правило марштутизации вызова
 * TODO описать поля и методы
 * */

@Entity
@Table(name = "managerclient")
@Data
@Getter
@Setter
public class Rule {
    @Id
    @GeneratedValue
    @JsonView(LogsViews.onlyId.class)
    private Long id;
    @Column(name = "client", nullable = false)
    @JsonView(LogsViews.forTable.class)
    private String clientNumber;
    @Column(name = "number", nullable = false)
    @JsonView(LogsViews.forTable.class)
    private String managerNumber;
    @Column(name = "FIOClient", nullable = false)
    @JsonView(LogsViews.forTable.class)
    private String clientName;
    /*
    * Наименование правила
    * */
    @Column(name = "name", nullable = false)
    @JsonView(LogsViews.forTable.class)
    private String name;



    public Rule(){

    }

    public Rule(String clientTelephone, String managerNumber, String clientName, String name){
        this.clientNumber = clientTelephone;
        this.managerNumber = managerNumber;
        this.clientName = clientName;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
