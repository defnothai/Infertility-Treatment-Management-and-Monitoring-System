package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.ServiceStatus;
import com.fuhcm.swp391.be.itmms.dto.request.ServiceRequest;
import com.fuhcm.swp391.be.itmms.dto.response.AccountBasic;
import com.fuhcm.swp391.be.itmms.dto.response.ServiceResponseHomePage;
import com.fuhcm.swp391.be.itmms.dto.response.ServiceResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.repository.ServiceRepository;
import com.fuhcm.swp391.be.itmms.utils.SlugUtil;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;

    public ServiceService(ServiceRepository serviceRepository,
                          ModelMapper modelMapper,
                          AuthenticationService authenticationService,
                          DoctorService doctorService) {
        this.serviceRepository = serviceRepository;
        this.modelMapper = modelMapper;
        this.authenticationService = authenticationService;
    }

    public com.fuhcm.swp391.be.itmms.entity.service.Service findById(Long id) throws NotFoundException {
        return serviceRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Dịch vụ không tồn tại"));
    }

    public List<ServiceResponseHomePage> getAllServicesInHomePage() {
        List<com.fuhcm.swp391.be.itmms.entity.service.Service> services = serviceRepository.findByStatusNot(ServiceStatus.DEPRECATED);
        List<ServiceResponseHomePage> serviceResponseHomePages = new ArrayList<>();
        for (com.fuhcm.swp391.be.itmms.entity.service.Service service : services) {
            serviceResponseHomePages.add(modelMapper.map(service, ServiceResponseHomePage.class));
        }
        return serviceResponseHomePages;
    }

    public List<ServiceResponse> getAllServicesInListPage() {
        List<com.fuhcm.swp391.be.itmms.entity.service.Service> services = serviceRepository.findByStatusNot(ServiceStatus.DEPRECATED);
        List<ServiceResponse> serviceResponse = new ArrayList<>();
        for (com.fuhcm.swp391.be.itmms.entity.service.Service service : services) {
            serviceResponse.add(modelMapper.map(service, ServiceResponse.class));
        }
        return serviceResponse;
    }

    public List<ServiceResponse> getAllServices() {
        List<com.fuhcm.swp391.be.itmms.entity.service.Service> services;
        Account currentAccount = authenticationService.getCurrentAccount();
        if (authenticationService.getCurrentRoles().contains("ROLE_ADMIN")) {
            services = serviceRepository.findAll();
        }else {
            services = serviceRepository.findByAccount(currentAccount);
        }
        List<ServiceResponse> responses = new ArrayList<>();
        for (com.fuhcm.swp391.be.itmms.entity.service.Service service : services) {
            responses.add(modelMapper.map(service, ServiceResponse.class));
        }
        return responses;
    }

    public ServiceResponse createService(ServiceRequest serviceRequest) throws IOException {
        com.fuhcm.swp391.be.itmms.entity.service.Service service =
                modelMapper.map(serviceRequest, com.fuhcm.swp391.be.itmms.entity.service.Service.class);
        service.setSlug(SlugUtil.toSlug(service.getServiceName()));
        Account currentAccount = authenticationService.getCurrentAccount();

        ServiceResponse response = new ServiceResponse();
        if (!authenticationService.getCurrentRoles().contains("ROLE_ADMIN")) {
            service.setAccount(currentAccount);
        }else {
            service.setAccount(authenticationService.findByEmail(serviceRequest.getManagerAccount().getEmail()));
        }
        response = modelMapper.map(serviceRepository.save(service), ServiceResponse.class);
        response.setManagerAccount(modelMapper.map(currentAccount, AccountBasic.class));
        return response;
    }

    public ServiceResponse updateService(Long id,
                                         ServiceRequest serviceRequest) throws NotFoundException, IOException {
        com.fuhcm.swp391.be.itmms.entity.service.Service service = this.findById(id);
        modelMapper.map(serviceRequest, service);
        service.setSlug(SlugUtil.toSlug(service.getServiceName()));
        ServiceResponse response = modelMapper.map(serviceRepository.save(service), ServiceResponse.class);
        return response;
    }
}
