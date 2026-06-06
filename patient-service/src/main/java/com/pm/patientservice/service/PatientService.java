package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientDTO;
import com.pm.patientservice.models.Patient;
import com.pm.patientservice.repos.PatientRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepo patientRepo;
    private final ModelMapper modelMapper;

    public List<PatientDTO> getAllPatients() {
        return patientRepo.findAll().stream().map(p -> modelMapper.map(p, PatientDTO.class)).toList();
    }

    public PatientDTO createPatient(PatientDTO dto) {
        if (patientRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        Patient patient = modelMapper.map(dto, Patient.class);
        patient.setId(null);
        return modelMapper.map(patientRepo.save(patient), PatientDTO.class);
    }

    public PatientDTO findByEmail(String email) {
        Patient patient = patientRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        return modelMapper.map(patient, PatientDTO.class);
    }

    public PatientDTO updatePatient(UUID id, PatientDTO dto) {
        Patient existing = patientRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        modelMapper.map(dto, existing);
        existing.setId(id);
        return modelMapper.map(patientRepo.save(existing), PatientDTO.class);
    }

    public void deletePatient(UUID id) {
        if (!patientRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found");
        }
        patientRepo.deleteById(id);
    }
}
