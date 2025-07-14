package com.fuhcm.swp391.be.itmms.service;
import com.fuhcm.swp391.be.itmms.dto.LabTestDTO;
import com.fuhcm.swp391.be.itmms.entity.lab.LabTest;
import com.fuhcm.swp391.be.itmms.repository.LabTestRepository;
import javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabTestService {

    private final LabTestRepository labTestRepository;
    private final ModelMapper modelMapper;

    public LabTestService(LabTestRepository labTestRepository, ModelMapper modelMapper) {
        this.labTestRepository = labTestRepository;
        this.modelMapper = modelMapper;
    }


    public List<LabTestDTO> getLabTests() {
        return labTestRepository
                .findAll()
                .stream()
                .map(labTest -> modelMapper.map(labTest, LabTestDTO.class))
                .toList();
    }

    public LabTest findById(long id) throws NotFoundException {
        return labTestRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Yêu cầu xét nghiệm không tồn tại"));
    }
}
