package com.java.xval.val;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class ValApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValApplication.class, args);
    }

}
