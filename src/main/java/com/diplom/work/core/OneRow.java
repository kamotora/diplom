package com.diplom.work.core;

import javax.persistence.*;

/**
 * Правило марштутизации вызова
 * TODO описать поля и методы
 * */

@Entity
@Table(name = "managerclient")
public class OneRow {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "client")
    private String client;
    @Column(name = "number")
    private String number;
    @Column(name = "FIOClient")
    private String FIOClient;

    public OneRow(){

    }

    public OneRow(String clientTelephone, String number, String FIOClient){
        this.client = clientTelephone;
        this.number = number;
        this.FIOClient = FIOClient;
    }

    public Integer getId() {
        return this.id;
    }

    public String getClient() {
        return this.client;
    }

    public String getNumber() {
        return this.number;
    }

    public String getFIOClient() {
        return this.FIOClient;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setFIOClient(String FIOClient) {
        this.FIOClient = FIOClient;
    }
}
