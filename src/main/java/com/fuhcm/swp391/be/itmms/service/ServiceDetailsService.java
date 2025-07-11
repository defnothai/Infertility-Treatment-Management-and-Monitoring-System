package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.ServiceDetailsRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.ServiceDetailsResponse;
import com.fuhcm.swp391.be.itmms.entity.service.ServiceDetails;
import com.fuhcm.swp391.be.itmms.repository.ServiceDetailsRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ServiceDetailsService {

    private final ServiceDetailsRepository serviceDetailsRepository;
    private final ModelMapper modelMapper;
    private final UploadImageFile uploadImageFile;
    private final ServiceService serviceService;

    public ServiceDetailsService(ServiceDetailsRepository serviceDetailsRepository,
                                 ModelMapper modelMapper,
                                 UploadImageFile uploadImageFile,
                                 ServiceService serviceService) {
        this.serviceDetailsRepository = serviceDetailsRepository;
        this.modelMapper = modelMapper;
        this.uploadImageFile = uploadImageFile;
        this.serviceService = serviceService;
    }

    public ServiceDetails findByServiceId(Long serviceId) throws NotFoundException {
        return serviceDetailsRepository.findByServiceId(serviceId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chi tiết dịch vụ"));
    }

    public ServiceDetails findById(Long id) throws NotFoundException {
        return serviceDetailsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chi tiết dịch vụ"));
    }

    public ServiceDetailsResponse getServiceDetails(Long serviceId) throws NotFoundException {
        ServiceDetails serviceDetails = findByServiceId(serviceId);
        ServiceDetailsResponse response = modelMapper.map(serviceDetails, ServiceDetailsResponse.class);
        response.setSlug(serviceDetails.getService().getSlug());
        response.setServiceName(serviceDetails.getService().getServiceName());
        return response;
    }


    public ServiceDetailsResponse createServiceDetails(Long serviceId,
                                                                       ServiceDetailsRequest serviceDetailsRequest) throws IOException, NotFoundException {
        ServiceDetails serviceDetails = modelMapper.map(serviceDetailsRequest, ServiceDetails.class);
        serviceDetails.setService(serviceService.findById(serviceId));
        ServiceDetailsResponse response = modelMapper.map(serviceDetailsRepository.save(serviceDetails), ServiceDetailsResponse.class);
        response.setSlug(serviceDetails.getService().getSlug());
        response.setServiceName(serviceDetails.getService().getServiceName());
        return response;
    }


    public ServiceDetailsResponse updateServiceDetails(Long id,
                                               ServiceDetailsRequest serviceDetailsRequest) throws NotFoundException {
        ServiceDetails serviceDetails = this.findById(id);
        modelMapper.map(serviceDetailsRequest, serviceDetails);
        serviceDetails.setId(id);
        ServiceDetailsResponse response = modelMapper.map(serviceDetailsRepository.save(serviceDetails), ServiceDetailsResponse.class);
        response.setSlug(serviceDetails.getService().getSlug());
        response.setServiceName(serviceDetails.getService().getServiceName());
        return response;
    }
}
