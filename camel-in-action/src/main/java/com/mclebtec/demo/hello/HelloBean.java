package com.mclebtec.demo.hello;


import org.springframework.stereotype.Component;

@Component
public class HelloBean {
    public String hello(String name) {
        System.out.println("Invoking HelloBean with " + name);
        return "Hello " + name;
    }
}
