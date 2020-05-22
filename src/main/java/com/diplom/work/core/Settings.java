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
    /**
     * Уникальный код идентификации (из ЛК ВАТС)
     * */
    private String clientID;
    /**
     * Уникальный ключ для подписи (из ЛК ВАТС)
     * */
    private String clientKey;
    /**
     * Нужно ли проверять подпись запроса при его приёме от ВАТС
     * Про подпись запроса написано у Ростелекома
     * */
    private Boolean isNeedCheckSign;
}
