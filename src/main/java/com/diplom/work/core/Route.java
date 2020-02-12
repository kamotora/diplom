package com.diplom.work.core;


import javax.persistence.*;

@Entity
@Table(name = "route")
public class Route {
    private Long id;
    private String from;
    private String to;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    @Column(nullable = false)
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return String.format("From %s to %s", from,to);
    }
}
