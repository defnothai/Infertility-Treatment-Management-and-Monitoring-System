package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.response.ServiceDetailsResponse;
import com.fuhcm.swp391.be.itmms.entity.service.ServiceDetails;
import com.fuhcm.swp391.be.itmms.repository.ServiceDetailsRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ServiceDetailsService {

    private final ServiceDetailsRepository serviceDetailsRepository;
    private final ModelMapper modelMapper;

    public ServiceDetailsService(ServiceDetailsRepository serviceDetailsRepository, ModelMapper modelMapper) {
        this.serviceDetailsRepository = serviceDetailsRepository;
        this.modelMapper = modelMapper;
    }

    public ServiceDetails findById(Long serviceId) throws NotFoundException {
        return serviceDetailsRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service details not found"));
    }

    public ResponseEntity getServiceDetails(Long serviceId) throws NotFoundException {
        ServiceDetails serviceDetails = this.findById(serviceId);
        ServiceDetailsResponse serviceDetailsResponse = modelMapper.map(serviceDetails, ServiceDetailsResponse.class);
        serviceDetailsResponse.setSlug(serviceDetails.getService().getSlug());
        return ResponseEntity.ok(serviceDetailsResponse);
    }


}
