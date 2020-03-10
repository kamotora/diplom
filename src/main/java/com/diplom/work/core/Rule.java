package com.diplom.work.core;

import javax.persistence.*;

/**
 * Правило марштутизации вызова
 * TODO описать поля и методы
 * */

@Entity
@Table(name = "managerclient")
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

    public Integer getId() {
        return this.id;
    }

    public String getClientNumber() {
        return this.clientNumber;
    }

    public String getManagerNumber() {
        return this.managerNumber;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setClientNumber(String client) {
        this.clientNumber = client;
    }

    public void setManagerNumber(String number) {
        this.managerNumber = number;
    }

    public void setClientName(String FIOClient) {
        this.clientName = FIOClient;
    }
}
