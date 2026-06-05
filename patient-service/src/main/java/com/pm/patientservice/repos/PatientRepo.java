package com.pm.patientservice.repos;

import com.pm.patientservice.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepo extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByEmail(String email);
}
