package com.imcode.entities;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by vitaly on 14.05.15.
 */
@Entity
@Table(name = "dbo_pupil")
public class Pupil extends AbstractIdEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pupilId")
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schoolClassId")
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "academicYearId")
    private AcademicYear academicYear;

    @OneToMany(mappedBy = "pupil")
    private Set<Truancy> truancies;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
    }

    public Set<Truancy> getTruancies() {
        return truancies;
    }

    public void setTruancies(Set<Truancy> truancies) {
        this.truancies = truancies;
    }
}
