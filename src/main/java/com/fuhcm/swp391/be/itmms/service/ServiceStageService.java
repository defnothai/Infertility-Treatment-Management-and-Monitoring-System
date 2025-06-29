package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.request.ServiceStageRequest;
import com.fuhcm.swp391.be.itmms.dto.response.ServiceStageResponse;
import com.fuhcm.swp391.be.itmms.entity.service.ServiceStage;
import com.fuhcm.swp391.be.itmms.repository.ServiceStageRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceStageService {

    private final ServiceStageRepository serviceStageRepository;
    private final ModelMapper modelMapper;
    private final ServiceService serviceService;

    public ServiceStageService(ServiceStageRepository serviceStageRepository,
                               ModelMapper modelMapper,
                               ServiceService serviceService) {
        this.serviceStageRepository = serviceStageRepository;
        this.modelMapper = modelMapper;
        this.serviceService = serviceService;
    }

    public List<ServiceStage> findByServiceId(Long serviceId) throws NotFoundException {
        List<ServiceStage> stages = serviceStageRepository.findByServiceIdAndIsActiveIsTrue(serviceId);
        if (stages == null) throw new NotFoundException("Các giai đoạn dịch vụ không tồn tại");
        return stages;
    }

    public ServiceStage findById(Long id) throws NotFoundException {
        return serviceStageRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Giai đoạn dịch vụ không tồn tại"));
    }

    public int findMaxStageOrder(Long serviceId) {
        return serviceStageRepository.findMaxStageOrderByServiceId(serviceId)
                .orElse(0);
    }

    public List<ServiceStageResponse> createServiceStages(Long serviceId,
                                                  List<ServiceStageRequest> stagesRequest) throws NotFoundException {
        int currentOrderStage = this.findMaxStageOrder(serviceId);
        List<ServiceStage> serviceStages = new ArrayList<>();
        for (ServiceStageRequest stage : stagesRequest) {
            ServiceStage serviceStage = modelMapper.map(stage, ServiceStage.class);
            serviceStage.setStageOrder(++currentOrderStage);
            serviceStage.setActive(true);
            serviceStage.setService(serviceService.findById(serviceId));
            serviceStages.add(serviceStage);
        }
        return serviceStageRepository
                .saveAll(serviceStages)
                .stream()
                .map(stage -> modelMapper.map(stage, ServiceStageResponse.class))
                .toList();
    }


    public ServiceStageResponse updateServiceStage(Long stageId,
                                                   ServiceStageRequest request) throws NotFoundException {
        ServiceStage stage = this.findById(stageId);
        modelMapper.map(request, stage);
        return modelMapper.map(serviceStageRepository.save(stage), ServiceStageResponse.class);
    }

    public void nomalizeStageOrder(Long serviceId) throws NotFoundException {
        List<ServiceStage> stages = serviceStageRepository.findByServiceIdAndIsActiveIsTrue(serviceId);

        int order = 0;
        for (ServiceStage stage : stages) {
            stage.setStageOrder(++order);
            serviceStageRepository.save(stage);
        }
    }

    public void deleteServiceStage(Long stageId) throws NotFoundException {
        ServiceStage stage = this.findById(stageId);
        stage.setActive(false);
        serviceStageRepository.save(stage);
        this.nomalizeStageOrder(stage.getService().getId());
    }

    public List<ServiceStageResponse> getAllServiceStages(Long serviceId) throws NotFoundException {
        return this.findByServiceId(serviceId)
                .stream()
                .map(stage -> modelMapper.map(stage, ServiceStageResponse.class))
                .toList();
    }

}
