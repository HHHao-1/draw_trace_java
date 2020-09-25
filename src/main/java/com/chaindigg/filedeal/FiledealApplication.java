package com.chaindigg.filedeal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class FiledealApplication {

    public static void main(String[] args) {
        SpringApplication.run(FiledealApplication.class, args);
    }

}
