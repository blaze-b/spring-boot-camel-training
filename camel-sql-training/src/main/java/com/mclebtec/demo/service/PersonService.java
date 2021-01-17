package com.mclebtec.demo.service;

import com.mclebtec.demo.model.Person;
import com.mclebtec.demo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {

    @Autowired
    PersonRepository repository;

    /**
     * Paging without sorting
     *
     * @param pageNo
     * @param pageSize
     * @return
     */

    public List<Person> getAllEmployees(Integer pageNo, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<Person> pagedResult = repository.findAll(paging);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<Person>();
        }
    }

    /**
     * Paging with sorting
     *
     * @param pageNo
     * @param pageSize
     * @param id
     * @return
     */
    public List<Person> getAllEmployees(Integer pageNo, Integer pageSize, String id) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(id));
        Page<Person> pagedResult = repository.findAll(paging);
        if (pagedResult.hasContent())
            return pagedResult.getContent();
        else
            return new ArrayList<Person>();
    }
}
