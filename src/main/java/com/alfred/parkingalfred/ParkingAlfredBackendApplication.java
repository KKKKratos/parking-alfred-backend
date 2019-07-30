package com.alfred.parkingalfred;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ParkingAlfredBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingAlfredBackendApplication.class, args);
    }

}
