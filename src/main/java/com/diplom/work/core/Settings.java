package com.diplom.work.core;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
@Data
@NoArgsConstructor
public class Settings {
    @Id
    @GeneratedValue
    private Long id;
    private String clientID;
    private String clientKey;
}
