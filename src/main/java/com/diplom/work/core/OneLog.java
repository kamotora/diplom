package com.diplom.work.core;

import javax.persistence.*;

@Entity
@Table(name = "logs")
public class OneLog {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "session_id")
    private String session;
    //@Column(name = "timestamp")
    //private String timestamp;
    @Column(name = "type")
    private String type;
    @Column(name = "state")
    private String state;
    @Column(name = "from_number")
    private String from_number;
    //@Column(name = "from_pin")
    //private String from_pin;
    @Column(name = "request_number")
    private String request_number;
    //@Column(name = "request_pin")
    //private String request_pin;


    public OneLog(){

    }

    public OneLog(String session_id, String type, String state, String from_number, String request_number){
        this.session = session_id;
        this.type = type;
        this.state = state;
        this.from_number = from_number;
        this.request_number = request_number;
    }

    public int getId() {
        return this.id;
    }

    public String getSession() {
        return this.session;
    }

    public String getType() {
        return this.type;
    }

    public String getState() {
        return this.state;
    }

    public String getFrom_number() {
        return this.from_number;
    }

    public String getRequest_number() {
        return this.request_number;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setFrom_number(String from_number) {
        this.from_number = from_number;
    }

    public void setRequest_number(String request_number) {
        this.request_number = request_number;
    }
}
