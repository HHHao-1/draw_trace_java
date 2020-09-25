package com.chaindigg.TransferAtlas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class TransferAtlasApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferAtlasApplication.class, args);
    }

}
