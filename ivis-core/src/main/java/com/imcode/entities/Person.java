package com.imcode.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imcode.entities.embed.Address;
import com.imcode.entities.embed.Email;
import com.imcode.entities.embed.Phone;
import com.imcode.entities.enums.AddressTypeEnum;
import com.imcode.entities.superclasses.AbstractIdEntity;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import javax.persistence.Entity;

/**
 * Created by vitaly on 13.05.15.
 */
@Entity
@Table(name = "dbo_person")
public class Person extends AbstractIdEntity<Long> implements Serializable {
    @Column
    private String personalId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @LazyCollection(LazyCollectionOption.FALSE)
//    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})//(fetch = FetchType.EAGER)
//    @JoinTable(name = "dbo_person_address_cross",
//            joinColumns = @JoinColumn(name = "personId", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "addressId", referencedColumnName = "id"))


    @ElementCollection
    @CollectionTable(name = "dbo_person_address", joinColumns = @JoinColumn(name = "ownerId"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name="addressTypeKey")
    private Map<AddressTypeEnum, Address> addresses;


//    @ElementCollection
//    @CollectionTable(name = "dbo_person_address", joinColumns = @JoinColumn(name = "ownerId"))
//    private Map<AddressTypeEnum, Address> addresses;

    @LazyCollection(LazyCollectionOption.FALSE)
//    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})//(fetch = FetchType.EAGER)
//    @JoinTable(name = "dbo_person_email_cross",
//            joinColumns = @JoinColumn(name = "personId"),
//            inverseJoinColumns = @JoinColumn(name = "emailId"))
    @ElementCollection
    @CollectionTable(name = "dbo_person_email", joinColumns = @JoinColumn(name = "ownerId"))
    private List<Email> emails;

    @LazyCollection(LazyCollectionOption.FALSE)
//    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
//    @JoinTable(name = "dbo_person_phone_cross",
//            joinColumns = @JoinColumn(name = "personId"),
//            inverseJoinColumns = @JoinColumn(name = "phoneId"))
    @ElementCollection
    @CollectionTable(name = "dbo_person_phone", joinColumns = @JoinColumn(name = "ownerId"))
    private List<Phone> phones;

//    private Set<Phone> phones;

    public Person(){
    }

    public Person(String pid, String firstName, String lastName) {
        this.personalId = pid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Map<AddressTypeEnum, Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Map<AddressTypeEnum, Address> addresses) {
        if (!(addresses instanceof EnumMap)) {
             addresses = new EnumMap<>(addresses);

        }

        this.addresses = addresses;
    }

    @JsonIgnore
    public Address getRegistredAddress() {
        return getAddress(AddressTypeEnum.REGISTERED);
    }

    @JsonIgnore
    public Address getResidentalAddress() {
        return getAddress(AddressTypeEnum.RESIDENTIAL);
    }

    @JsonIgnore
    public Address getBoarderdAddress() {
        return getAddress(AddressTypeEnum.BOARDER);
    }


    @JsonIgnore
    public Address getAddress(AddressTypeEnum addressType) {
        Objects.requireNonNull(addressType);

        if (addresses == null) {
            return null;
        }

        return addresses.get(addressType);
    }

    @JsonIgnore
    public void setAddress(Address address) {
        Objects.requireNonNull(address);
        Objects.requireNonNull(address.getAddressType());

        if (addresses == null) {
            addresses = new EnumMap<>(AddressTypeEnum.class);
        }

        addresses.put(address.getAddressType(), address);
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

//    public List<Phone> getPhoneList() {
//        return new LinkedList<>(phones);
//    }
//
//    public void setPhoneList(List<Phone> phones) {
//        this.phones = new LinkedHashSet<>(phones);
//    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public static Person fromString(String firstNameLastName) {
        if (firstNameLastName == null || firstNameLastName.isEmpty()) {
            throw new IllegalArgumentException("The \"firstNameLastName\" should be not epmty!");
        }

        Person person = new Person();

        String[] parts = firstNameLastName.split(" ");

        person.setFirstName(parts[0]);
        try {
            person.setLastName(parts[1]);
        } catch (Exception ignore) {
        }


        return person;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(firstName))
            addWord(sb, firstName);

        if (StringUtils.hasText(lastName))
            addWord(sb, lastName);


        if (sb.length() == 0)
            addWord(sb, personalId);;

        return sb.toString();
    }


    private void addWord(StringBuilder sb, String word) {
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
            sb.append(' ');
        }

        sb.append(word);
    }
}
