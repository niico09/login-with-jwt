package com.santander.seclog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeclogApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeclogApplication.class, args);
    }

    /*  @Bean
    CommandLineRunner initDataBase(RoomRepository repository){
        return args -> {
            repository.save(new Room("SALA A4"));
            repository.save(new Room("SALA A5"));
        };
    } */
}
