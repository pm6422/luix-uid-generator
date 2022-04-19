package com.luixtech.uidgenerator.usage.demo;

import com.luixtech.uidgenerator.springboot.config.EnableUidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableUidGenerator
@Slf4j
public class LuixUidUsageDemoApplication {

    /**
     * Entrance method which used to run the application. Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(LuixUidUsageDemoApplication.class, args);
    }
}
