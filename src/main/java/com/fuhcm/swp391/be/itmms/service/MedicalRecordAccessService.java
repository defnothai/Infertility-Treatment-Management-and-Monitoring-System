package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.AccessRole;
import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.PermissionLevel;
import com.fuhcm.swp391.be.itmms.dto.request.MedicalRecordAccessRequest;
import com.fuhcm.swp391.be.itmms.dto.response.MedicalRecordAccessResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.doctor.Doctor;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecord;
import com.fuhcm.swp391.be.itmms.entity.medical.MedicalRecordAccess;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordAccessRepository;
import com.fuhcm.swp391.be.itmms.repository.MedicalRecordRepository;
import com.fuhcm.swp391.be.itmms.repository.RoleRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordAccessService {

    private final AuthenticationService authenticationService;
    private final MedicalRecordAccessRepository medicalRecordAccessRepository;
    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final MedicalRecordRepository medicalRecordRepository;
    private final AccountRepository accountRepository;

    public boolean isManager() {
        return authenticationService.getCurrentAccount().getRoles().contains(roleService.findByRoleName(AccountRole.ROLE_MANAGER));
    }

    public MedicalRecordAccess isAccessedToMedicalRecord(MedicalRecord medicalRecord) {
        Account currentAccount = authenticationService.getCurrentAccount();
        List<MedicalRecordAccess> accesses = medicalRecord.getMedicalRecordAccess();
        for (MedicalRecordAccess access : accesses) {
            if (access.getGrantedTo().getId().equals(currentAccount.getId()) &&
                    (access.getDayEnd() == null || access.getDayEnd().isAfter(LocalDate.now()) || access.getDayEnd().isEqual(LocalDate.now())) &&
                    (access.getDayStart().isBefore(LocalDate.now()) || access.getDayStart().isEqual(LocalDate.now()))) {
                return access;
            }
        }
        return null;
    }

    public boolean canView(MedicalRecord medicalRecord) {
        if (isManager()) {
            return true;
        }
        return isAccessedToMedicalRecord(medicalRecord) != null ;
    }

    public boolean canUpdate(MedicalRecord medicalRecord) {
        if (isManager()) {
            return true;
        }
        MedicalRecordAccess access = isAccessedToMedicalRecord(medicalRecord);
        return canView(medicalRecord) && (access.getLevel() == PermissionLevel.EDIT || access.getLevel() == PermissionLevel.FULL_ACCESS);
    }

    public boolean canCreate(MedicalRecord medicalRecord) {
        if (isManager()) {
            return true;
        }
        MedicalRecordAccess access = isAccessedToMedicalRecord(medicalRecord);
        return canView(medicalRecord) && (access.getLevel() == PermissionLevel.EDIT || access.getLevel() == PermissionLevel.FULL_ACCESS);
    }

    public boolean canDelete(MedicalRecord medicalRecord) {
        if (isManager()) {
            return true;
        }
        MedicalRecordAccess access = isAccessedToMedicalRecord(medicalRecord);
        return canView(medicalRecord) && access.getLevel() == PermissionLevel.FULL_ACCESS;
    }

    public List<MedicalRecordAccessResponse> getAccessListByMedicalRecordId(Long medicalRecordId) {
        List<MedicalRecordAccess> accessList = medicalRecordAccessRepository.findByMedicalRecord_Id(medicalRecordId);

        return accessList.stream().map(access -> {
            MedicalRecordAccessResponse dto = modelMapper.map(access, MedicalRecordAccessResponse.class);

            if (access.getGrantedBy() != null) {
                String grantedByName = "";
                Account grantedBy = access.getGrantedBy();
                if (grantedBy.getRoles().contains(roleService.findByRoleName(AccountRole.ROLE_MANAGER))) {
                    grantedByName += "Manager ";
                }
                if (grantedBy.getRoles().contains(roleService.findByRoleName(AccountRole.ROLE_DOCTOR))) {
                    grantedByName += "Doctor ";
                }
                dto.setGrantedBy(grantedByName + access.getGrantedBy().getFullName());
            }
            Account grantedTo = access.getGrantedTo();
            if (grantedTo != null && grantedTo.getDoctor() != null) {
                Doctor doctor = grantedTo.getDoctor();
                dto.setDoctorName(grantedTo.getFullName());
                dto.setExpertise(doctor.getExpertise());
                dto.setPosition(doctor.getPosition());
            }
            return dto;
        }).toList();
    }

    public MedicalRecordAccessResponse createAccess(Long medicalRecordId, MedicalRecordAccessRequest request) throws NotFoundException {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hồ sơ"));

        Account grantedBy = authenticationService.getCurrentAccount();

        Account grantedTo = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản bác sĩ"));

        if (medicalRecordAccessRepository.existsByMedicalRecordAndGrantedTo(medicalRecord, grantedTo)) {
            throw new IllegalStateException("Bác sĩ này đã được cấp quyền truy cập hồ sơ");
        }

        if (request.getRole() == AccessRole.MAIN_DOCTOR) {
            boolean mainDoctorExists = medicalRecordAccessRepository.existsByMedicalRecordAndRole(medicalRecord, AccessRole.MAIN_DOCTOR);
            if (mainDoctorExists) {
                throw new IllegalStateException("Đã có bác sĩ điều trị chính cho hồ sơ này");
            }
        }

        MedicalRecordAccess access = modelMapper.map(request, MedicalRecordAccess.class);
        access.setMedicalRecord(medicalRecord);
        access.setGrantedBy(grantedBy);
        access.setGrantedTo(grantedTo);
        access = medicalRecordAccessRepository.save(access);
        MedicalRecordAccessResponse response = modelMapper.map(access, MedicalRecordAccessResponse.class);
        Doctor doctor = grantedTo.getDoctor();
        response.setGrantedBy(grantedBy.getFullName());
        response.setDoctorName(grantedTo.getFullName());
        response.setExpertise(doctor.getExpertise());
        response.setPosition(doctor.getPosition());
        return response;
    }

    public MedicalRecordAccessResponse updateAccess(Long accessId, MedicalRecordAccessRequest request) throws NotFoundException {
        MedicalRecordAccess access = medicalRecordAccessRepository.findById(accessId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền truy cập hồ sơ"));

        if (request.getRole() == AccessRole.MAIN_DOCTOR) {
            boolean mainDoctorExists = medicalRecordAccessRepository.existsByMedicalRecordAndRole(access.getMedicalRecord(), AccessRole.MAIN_DOCTOR);
            if (mainDoctorExists) {
                throw new IllegalStateException("Đã có bác sĩ điều trị chính cho hồ sơ này");
            }
        }

        modelMapper.map(request, access);
        medicalRecordAccessRepository.save(access);
        MedicalRecordAccessResponse response = modelMapper.map(access, MedicalRecordAccessResponse.class);
        Doctor doctor = access.getGrantedTo().getDoctor();
        response.setGrantedBy(access.getGrantedBy().getFullName());
        response.setDoctorName(access.getGrantedTo().getFullName());
        response.setExpertise(doctor.getExpertise());
        response.setPosition(doctor.getPosition());
        return response;
    }

}
