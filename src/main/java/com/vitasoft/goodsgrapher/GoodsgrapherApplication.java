package com.vitasoft.goodsgrapher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GoodsgrapherApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsgrapherApplication.class, args);
    }

}
