package com.imcode.entities.embed;

import com.imcode.entities.enums.AddressTypeEnum;
import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by vitaly on 13.05.15.
 */
//@Entity
//@Table(name = "dbo_address")
@Embeddable
public class Address implements Serializable {
    @Column
    private Integer postalCode;

    @Column
    private String municipalityCode;

    @Column(length = 50)
    private String city;

    @Column
    private String street;

    @Column
    private String street2;

    @Column
    private String propertyDescription;

    @Column
    private String careOf;

    @Column
    @Enumerated(EnumType.STRING)
    private AddressTypeEnum addressType;

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public String getMunicipalityCode() {
        return municipalityCode;
    }

    public void setMunicipalityCode(String municipality_code) {
        this.municipalityCode = municipality_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    public String getCareOf() {
        return careOf;
    }

    public void setCareOf(String careOf) {
        this.careOf = careOf;
    }

    public AddressTypeEnum getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressTypeEnum addressType) {
        this.addressType = addressType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder()
                .append(addressType)
                .append(postalCode)
                .append(city)
                .append('(')
                .append(municipalityCode)
                .append("), ")
                .append(street);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (postalCode != null ? !postalCode.equals(address.postalCode) : address.postalCode != null) return false;
        if (municipalityCode != null ? !municipalityCode.equals(address.municipalityCode) : address.municipalityCode != null)
            return false;
        if (city != null ? !city.equals(address.city) : address.city != null) return false;
        if (street != null ? !street.equals(address.street) : address.street != null) return false;
        if (street2 != null ? !street2.equals(address.street2) : address.street2 != null) return false;
        if (propertyDescription != null ? !propertyDescription.equals(address.propertyDescription) : address.propertyDescription != null)
            return false;
        if (careOf != null ? !careOf.equals(address.careOf) : address.careOf != null) return false;
        return addressType == address.addressType;

    }

    @Override
    public int hashCode() {
        int result = postalCode != null ? postalCode.hashCode() : 0;
        result = 31 * result + (municipalityCode != null ? municipalityCode.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        result = 31 * result + (street2 != null ? street2.hashCode() : 0);
        result = 31 * result + (propertyDescription != null ? propertyDescription.hashCode() : 0);
        result = 31 * result + (careOf != null ? careOf.hashCode() : 0);
        result = 31 * result + (addressType != null ? addressType.hashCode() : 0);

        return result;
    }
}
