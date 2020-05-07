package com.diplom.work.core;

import lombok.Data;

import javax.persistence.*;

/**
 * Правило марштутизации вызова
 * TODO описать поля и методы
 * */

@Entity
@Table(name = "managerclient")
@Data
public class Rule {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "client", nullable = false)
    private String clientNumber;
    @Column(name = "number", nullable = false)
    private String managerNumber;
    @Column(name = "FIOClient", nullable = false)
    private String clientName;

    public Rule(){

    }

    public Rule(String clientTelephone, String managerNumber, String clientName){
        this.clientNumber = clientTelephone;
        this.managerNumber = managerNumber;
        this.clientName = clientName;
    }
}
