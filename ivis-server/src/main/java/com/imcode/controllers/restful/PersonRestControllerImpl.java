package com.imcode.controllers.restful;

import com.imcode.controllers.AbstractRestController;
import com.imcode.entities.Person;
import com.imcode.services.PersonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/{format}/persons")
public class PersonRestControllerImpl extends AbstractRestController<Person, Long, PersonService> {
//    @RequestMapping(method = RequestMethod.GET, params = {"personalId"})
//    Person getPersonByPersonalId(@RequestParam("personalId") String personId,
//                           @RequestParam(value = "first", required = false) Boolean firstOnly) {
//        Person person = getService().findFirstByPersonalId(personId);
//        return person;
//    }


}
