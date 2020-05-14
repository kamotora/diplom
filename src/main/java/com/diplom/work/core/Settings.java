package com.diplom.work.core;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "settings")
@Data
@NoArgsConstructor
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clientID;
    private String clientKey;
    private Boolean isNeedCheckSign;
}
