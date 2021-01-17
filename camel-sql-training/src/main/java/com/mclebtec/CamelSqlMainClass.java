package com.mclebtec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.Data;

@Data
@SpringBootApplication
public class CamelSqlMainClass {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        SpringApplication.run(CamelSqlMainClass.class, args);
        while (true) {
        }
    }

}
