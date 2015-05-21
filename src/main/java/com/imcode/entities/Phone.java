package com.imcode.entities;

import com.imcode.entities.enums.CommunicationTypeEnum;

import javax.persistence.*;

/**
 * Created by vitaly on 13.05.15.
 */
@Entity
@Table(name = "dbo_phone")
public class Phone extends AbstractIdEntity {
    @Column
    private String number;

    @Enumerated(EnumType.STRING)
    private CommunicationTypeEnum communicationType;

    public Phone() {
    }

    public Phone(String number, CommunicationTypeEnum communicationType) {
        this.number = number;
        this.communicationType = communicationType;
    }

    public Phone(String number) {
        this.number = number;
        this.communicationType = CommunicationTypeEnum.MOBILE;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public CommunicationTypeEnum getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(CommunicationTypeEnum communicationType) {
        this.communicationType = communicationType;
    }
}
