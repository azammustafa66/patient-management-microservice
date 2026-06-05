package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientDTO;
import com.pm.patientservice.service.PatientService;
import com.pm.patientservice.utils.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping()
    public ResponseEntity<APIResponse<List<PatientDTO>>> getAllPatients() {
        List<PatientDTO> data = patientService.getAllPatients();
        return ResponseEntity.ok().body(new APIResponse<>(HttpStatus.OK.value(), data));
    }
}
