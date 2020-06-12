package com.diplom.work.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "settings")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Уникальный код идентификации (из ЛК ВАТС)
     */
    private String clientID;
    /**
     * Уникальный ключ для подписи (из ЛК ВАТС)
     */
    private String clientKey;
    /**
     * Нужно ли проверять подпись запроса при его приёме от ВАТС
     * Про подпись запроса написано у Ростелекома
     */
    private Boolean isNeedCheckSign;
    /**
     * Включена проверка токенов для /rest/***
     * Иначе доступ закрыт (доступ имеет только само приложение)
     */
    private Boolean isTokensActivate;
    /**
     * Пользователи (не администраторы) могут видеть
     * в таблице только правила со своим участием (они указаны в качестве менеджера)
     */
    private Boolean isUsersCanViewOnlyTheirRules;
    /**
     * Для пользователей (не администраторов) при добавлении правила
     * в графе "Менеджер" автоматически подставляется данный пользователь.
     * При этом он всё равно может выбрать "Умная маршрутизация".
     */
    private Boolean isUsersCanAddRulesOnlyMyself;
    /**
     * Пользователи (не администраторы, не статисты) могут видеть
     * в журнале вызовов только вызовы со своим участием (их номер указан
     * в качестве вызываемого или вызывающего абонента)
     */
    private Boolean isUsersCanViewLogOnlyMyself;
}
