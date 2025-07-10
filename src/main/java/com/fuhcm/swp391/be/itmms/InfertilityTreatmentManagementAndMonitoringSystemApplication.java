package com.fuhcm.swp391.be.itmms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class InfertilityTreatmentManagementAndMonitoringSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfertilityTreatmentManagementAndMonitoringSystemApplication.class, args);
    }

}
