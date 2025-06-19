package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.response.ServiceResponse;
import com.fuhcm.swp391.be.itmms.repository.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    public ServiceService(ServiceRepository serviceRepository,
                          ModelMapper modelMapper) {
        this.serviceRepository = serviceRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity getAllServicesInHomePage() {
        List<com.fuhcm.swp391.be.itmms.entity.service.Service> services = serviceRepository.findAll();
        List<ServiceResponse> serviceResponses = new ArrayList<>();
        for (com.fuhcm.swp391.be.itmms.entity.service.Service service : services) {
            serviceResponses.add(modelMapper.map(service, ServiceResponse.class));
        }
        return ResponseEntity.ok(serviceResponses);
    }

}
