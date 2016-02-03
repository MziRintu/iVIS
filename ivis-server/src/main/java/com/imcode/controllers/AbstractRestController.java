package com.imcode.controllers;

import com.imcode.misc.errors.ErrorFactory;
import com.imcode.services.GenericService;
import com.imcode.services.NamedService;
import com.imcode.services.PersonalizedService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vitaly on 17.02.15.
 */
public abstract class AbstractRestController<T, ID extends Serializable, SERVICE_TYPE extends GenericService<T, ID>> implements CrudController<T, ID>{

    @Autowired
    private SERVICE_TYPE service;

    @Autowired
    private ErrorFactory errorFactory;

    @Autowired
    private ApplicationContext ctx;

    // Getting entity by id
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object get(@PathVariable("id") ID id, WebRequest webRequest) {
        return service.find(id);
    }

    //Getting list of entities
    @RequestMapping(method = RequestMethod.GET)
    public Object getAll(WebRequest webRequest, Model model) {
        List<T> result = service.findAll();
        return result;
    }

    //Creating entity
    @RequestMapping(method = RequestMethod.POST)
//    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody Object create(@RequestBody T entity, WebRequest webRequest) {
        return service.save(entity);
    }

    // Updating entity
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Object update(@PathVariable("id") ID id, @RequestBody(required = false) T entity, WebRequest webRequest) {
        T existsEntity = getService().find(id);

        if (existsEntity != null) {
            BeanUtils.copyProperties(entity, existsEntity, "id");
            service.save(entity);
        }

        return existsEntity;
    }

    //Deleting entity
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") ID id, WebRequest webRequest) {
        service.delete(id);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET, params = {"name"})
    public Object getByName(WebRequest webRequest, Model model,
                         @RequestParam("name") String name,
                         @RequestParam(value = "first", required = false) Boolean firstOnly) {

        if (service instanceof NamedService) {
            NamedService<T> namedService = (NamedService<T>) service;

            if (firstOnly == null || !firstOnly) {
                return namedService.findByName(name);
            } else {
                return namedService.findFirstByName(name);
            }
        }

        throw new UnsupportedOperationException("findByName metod not supported!");

    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET, params = {"personalId"})
    public Object getByPersonalId(@RequestParam("personalId") String personId,
                                  @RequestParam(value = "first", required = false) Boolean firstOnly) {

        if (service instanceof PersonalizedService) {
            PersonalizedService<T> personalizedService = (PersonalizedService<T>) service;

            if (firstOnly == null || !firstOnly) {
                return personalizedService.findByPersonalId(personId);
            } else {
                return personalizedService.findFirstByPersonalId(personId);
            }
        }

        throw new UnsupportedOperationException("findByName metod not supported!");
    }

    // Getters & Setters
    //------------------------------------------------------------------------------------------------------------------
    public SERVICE_TYPE getService() {
        return service;
    }

    public void setService(SERVICE_TYPE service) {
        this.service = service;
    }

//    public ErrorFactory getErrorFactory() {
//        return errorFactory;
//    }
//
//    public void setErrorFactory(ErrorFactory errorFactory) {
//        this.errorFactory = errorFactory;
//    }
}
