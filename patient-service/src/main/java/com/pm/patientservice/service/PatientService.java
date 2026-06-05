package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientDTO;
import com.pm.patientservice.models.Patient;
import com.pm.patientservice.repos.PatientRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepo patientRepo;
    private final ModelMapper modelMapper;

    public List<PatientDTO> getAllPatients() {
        return patientRepo.findAll().stream().map(p -> modelMapper.map(p, PatientDTO.class)).toList();
    }
}
