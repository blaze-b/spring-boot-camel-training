package com.mclebtec.demo.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mclebtec.demo.model.Person;

public interface PersonRepository extends PagingAndSortingRepository<Person, Long>{
}
