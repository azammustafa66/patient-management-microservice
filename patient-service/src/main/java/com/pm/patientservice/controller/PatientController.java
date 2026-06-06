package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientDTO;
import com.pm.patientservice.dto.PatientLookupDTO;
import com.pm.patientservice.service.PatientService;
import com.pm.patientservice.utils.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<APIResponse<List<PatientDTO>>> getAllPatients() {
        List<PatientDTO> data = patientService.getAllPatients();
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), data));
    }

    @PostMapping
    public ResponseEntity<APIResponse<PatientDTO>> createPatient(@Valid @RequestBody PatientDTO dto) {
        PatientDTO created = patientService.createPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(HttpStatus.CREATED.value(), created));
    }

    @PostMapping("/find")
    public ResponseEntity<APIResponse<PatientDTO>> findByEmail(@Valid @RequestBody PatientLookupDTO lookup) {
        PatientDTO found = patientService.findByEmail(lookup.email());
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), found));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<PatientDTO>> updatePatient(@PathVariable UUID id,
                                                                 @Valid @RequestBody PatientDTO dto) {
        PatientDTO updated = patientService.updatePatient(id, dto);
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
