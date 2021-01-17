package com.mclebtec.demo.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mclebtec.demo.model.Person;
import com.mclebtec.demo.service.PersonService;

@Component
public class PersonDetailsExtractor {

	private PersonService personservice;
	private List messages = new ArrayList();
	private List<Person> personlist = new ArrayList();
	@Autowired
	public PersonDetailsExtractor(PersonService personservice) {
		this.personservice = personservice;
	}

	

	@SuppressWarnings("unchecked")
	public String hello(String msg) {
		String helloMsg = "Hello " + msg;
		System.out.println(helloMsg);
		messages.add(helloMsg);
		return helloMsg;
	}

	public void extractDetails(Exchange exchange, Integer pageNo, Integer pageSize) {
		System.out.println(pageNo);
		System.out.println(pageSize);
		personlist = personservice.getAllEmployees(pageNo, pageSize);
		System.out.println(personlist);
		exchange.getIn().setBody(personlist);
	}
	public String toString() {
		return personlist.toString();
	}

}
