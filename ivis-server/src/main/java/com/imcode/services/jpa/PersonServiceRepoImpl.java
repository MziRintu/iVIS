package com.imcode.services.jpa;

import com.imcode.entities.Person;
import com.imcode.repositories.PersonRepository;
import com.imcode.services.AbstractService;
import com.imcode.services.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceRepoImpl extends AbstractService<Person, Long, PersonRepository> implements PersonService {
    @Override
    public Person findFirstByPersonalId(String personalId) {
        return getRepo().findFirstByPersonalId(personalId);
    }

    @Override
    public List<Person> findByPersonalId(String personalId) {
        return getRepo().findByPersonalId(personalId);
    }

}
