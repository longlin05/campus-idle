package org.lin.campusidle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.lin.campusidle.mapper")
public class CampusIdleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusIdleApplication.class, args);
    }

}
