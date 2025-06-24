package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.ServiceStatus;
import com.fuhcm.swp391.be.itmms.dto.request.ServiceRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.dto.response.ServiceResponseHomePage;
import com.fuhcm.swp391.be.itmms.dto.response.ServiceResponse;
import com.fuhcm.swp391.be.itmms.repository.ServiceRepository;
import com.fuhcm.swp391.be.itmms.utils.SlugUtil;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;
    private final UploadImageFile uploadImageFile;

    public ServiceService(ServiceRepository serviceRepository,
                          ModelMapper modelMapper,
                          AuthenticationService authenticationService,
                          UploadImageFile uploadImageFile) {
        this.serviceRepository = serviceRepository;
        this.modelMapper = modelMapper;
        this.authenticationService = authenticationService;
        this.uploadImageFile = uploadImageFile;
    }

    public com.fuhcm.swp391.be.itmms.entity.service.Service findById(Long id) throws NotFoundException {
        return serviceRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Dịch vụ không tồn tại"));
    }

    public ResponseEntity getAllServicesInHomePage() {
        List<com.fuhcm.swp391.be.itmms.entity.service.Service> services = serviceRepository.findByStatusNot(ServiceStatus.DEPRECATED);
        List<ServiceResponseHomePage> serviceResponseHomePages = new ArrayList<>();
        for (com.fuhcm.swp391.be.itmms.entity.service.Service service : services) {
            serviceResponseHomePages.add(modelMapper.map(service, ServiceResponseHomePage.class));
        }
        return ResponseEntity.ok(serviceResponseHomePages);
    }

    public ResponseEntity getAllServicesInListPage() {
        List<com.fuhcm.swp391.be.itmms.entity.service.Service> services = serviceRepository.findByStatusNot(ServiceStatus.DEPRECATED);
        List<ServiceResponse> serviceRespons = new ArrayList<>();
        for (com.fuhcm.swp391.be.itmms.entity.service.Service service : services) {
            serviceRespons.add(modelMapper.map(service, ServiceResponse.class));
        }
        return ResponseEntity.ok(serviceRespons);
    }

    public ResponseEntity getAllServices() {
        List<com.fuhcm.swp391.be.itmms.entity.service.Service> services = serviceRepository.findAll();
        List<ServiceResponse> serviceResponse = new ArrayList<>();
        for (com.fuhcm.swp391.be.itmms.entity.service.Service service : services) {
            serviceResponse.add(modelMapper.map(service, ServiceResponse.class));
        }
        return ResponseEntity.ok(serviceResponse);
    }

    public ResponseEntity<ResponseFormat<Object>> createService(ServiceRequest serviceRequest) throws IOException {
        String imgUrl = uploadImageFile.uploadImage(serviceRequest.getImage());
        com.fuhcm.swp391.be.itmms.entity.service.Service service =
                modelMapper.map(serviceRequest, com.fuhcm.swp391.be.itmms.entity.service.Service.class);
        service.setImgUrl(imgUrl);
        service.setSlug(SlugUtil.toSlug(service.getServiceName()));
        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                    "SERVICE_CREATED_SUCCESS",
                                    "Tạo mới dịch vụ thành công",
                                    modelMapper.map(serviceRepository.save(service), ServiceResponse.class)));
    }

    public ResponseEntity<ResponseFormat<Object>> updateService(Long id,
                                                                ServiceRequest serviceRequest) throws NotFoundException, IOException {
        String imgUrl = uploadImageFile.uploadImage(serviceRequest.getImage());
        com.fuhcm.swp391.be.itmms.entity.service.Service service = this.findById(id);
        service.setServiceName(serviceRequest.getServiceName());
        service.setSubTitle(serviceRequest.getSubTitle());
        service.setPrice(serviceRequest.getPrice());
        service.setSummary(serviceRequest.getSummary());
        service.setImgUrl(imgUrl);
        service.setSlug(SlugUtil.toSlug(service.getServiceName()));
        service.setStatus(serviceRequest.getStatus());
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                    "SERVICE_UPDATED_SUCCESS",
                                    "Cập nhật dịch vụ thành công",
                                    modelMapper.map(serviceRepository.save(service), ServiceResponse.class)));

    }

}
