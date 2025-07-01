package com.fuhcm.swp391.be.itmms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InfertilityTreatmentManagementAndMonitoringSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfertilityTreatmentManagementAndMonitoringSystemApplication.class, args);
    }

}
