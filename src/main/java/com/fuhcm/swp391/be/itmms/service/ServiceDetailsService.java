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

    public ServiceDetails findById(Long serviceId) throws NotFoundException {
        return serviceDetailsRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chi tiết dịch vụ"));
    }

    public ResponseEntity getServiceDetails(Long serviceId) throws NotFoundException {
        return null;
    }


    public ResponseEntity<ResponseFormat<Object>> createServiceDetails(Long serviceId,
                                                               ServiceDetailsRequest serviceDetailsRequest) throws IOException, NotFoundException {
        String conceptImgUrl = uploadImageFile.uploadImage(serviceDetailsRequest.getConceptImage());
        String procedureDetailsImgUrl = uploadImageFile.uploadImage(serviceDetailsRequest.getProcedureDetailsImage());
        String hospitalProcedureImgUrl = uploadImageFile.uploadImage(serviceDetailsRequest.getHospitalProcedureImage());
        ServiceDetails serviceDetails = modelMapper.map(serviceDetailsRequest, ServiceDetails.class);
        serviceDetails.setConceptImgUrl(conceptImgUrl);
        serviceDetails.setProcedureDetails(procedureDetailsImgUrl);
        serviceDetails.setHospitalProcedureImgUrl(hospitalProcedureImgUrl);
        serviceDetails.setService(serviceService.findById(serviceId));
        ServiceDetailsResponse response = modelMapper.map(serviceDetailsRepository.save(serviceDetails), ServiceDetailsResponse.class);
        response.setSlug(serviceDetails.getService().getSlug());
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                                        "SERVICE_DETAILS_CREATED_SUCCESS",
                                                        "Tạo mới chi tiết dịch vụ thành công",
                                                        response));
    }
}
