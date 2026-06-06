package com.pm.patientservice.integration;

import com.pm.patientservice.dto.PatientDTO;
import com.pm.patientservice.repos.PatientRepo;
import com.pm.patientservice.utils.APIResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
public class PatientApiIntegrationTest {

    private static final ParameterizedTypeReference<APIResponse<PatientDTO>> SINGLE_REF =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<APIResponse<List<PatientDTO>>> MULTI_REF =
            new ParameterizedTypeReference<>() {};

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PatientRepo patientRepo;

    @BeforeEach
    void clearDb() {
        patientRepo.deleteAll();
    }

    private Map<String, Object> validPayload() {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Jane");
        body.put("lastName", "Doe");
        body.put("gender", "Female");
        body.put("email", "jane@example.com");
        body.put("address", "1 Test St");
        body.put("birthDate", LocalDate.of(1990, 1, 1).toString());
        body.put("registrationDate", LocalDate.now().toString());
        return body;
    }

    private String createPatient(Map<String, Object> payload) {
        ResponseEntity<APIResponse<PatientDTO>> r = restTemplate.exchange(
                "/api/patients", HttpMethod.POST, new HttpEntity<>(payload), SINGLE_REF);
        assertThat(r.getStatusCode().value()).isEqualTo(201);
        assertThat(r.getBody()).isNotNull();
        return r.getBody().getData().getId().toString();
    }

    @Test
    void create_returns201_andPersists() {
        ResponseEntity<APIResponse<PatientDTO>> response = restTemplate.exchange(
                "/api/patients", HttpMethod.POST, new HttpEntity<>(validPayload()), SINGLE_REF);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getId()).isNotNull();
        assertThat(patientRepo.count()).isEqualTo(1);
    }

    @Test
    void create_returns400_whenEmailMissing() {
        Map<String, Object> invalid = validPayload();
        invalid.remove("email");

        ResponseEntity<APIResponse<PatientDTO>> response = restTemplate.exchange(
                "/api/patients", HttpMethod.POST, new HttpEntity<>(invalid), SINGLE_REF);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("email");
    }

    @Test
    void create_returns409_onDuplicateEmail() {
        createPatient(validPayload());

        ResponseEntity<APIResponse<PatientDTO>> response = restTemplate.exchange(
                "/api/patients", HttpMethod.POST, new HttpEntity<>(validPayload()), SINGLE_REF);

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("Email already in use");
    }

    @Test
    void list_returnsAllPatients() {
        createPatient(validPayload());

        ResponseEntity<APIResponse<List<PatientDTO>>> response = restTemplate.exchange(
                "/api/patients", HttpMethod.GET, null, MULTI_REF);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
    }

    @Test
    void findByEmail_returns200_forExistingPatient() {
        createPatient(validPayload());

        ResponseEntity<APIResponse<PatientDTO>> response = restTemplate.exchange(
                "/api/patients/find", HttpMethod.POST,
                new HttpEntity<>(Map.of("email", "jane@example.com")), SINGLE_REF);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        PatientDTO data = response.getBody().getData();
        assertThat(data.getFirstName()).isEqualTo("Jane");
        assertThat(data.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void findByEmail_returns404_forUnknownEmail() {
        ResponseEntity<APIResponse<PatientDTO>> response = restTemplate.exchange(
                "/api/patients/find", HttpMethod.POST,
                new HttpEntity<>(Map.of("email", "nobody@example.com")), SINGLE_REF);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("Patient not found");
    }

    @Test
    void update_returns200_andPersistsChanges() {
        String id = createPatient(validPayload());
        Map<String, Object> updated = validPayload();
        updated.put("lastName", "Smith");

        ResponseEntity<APIResponse<PatientDTO>> response = restTemplate.exchange(
                "/api/patients/" + id, HttpMethod.PUT, new HttpEntity<>(updated), SINGLE_REF);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(patientRepo.findAll().getFirst().getLastName()).isEqualTo("Smith");
    }

    @Test
    void update_returns404_forUnknownId() {
        String fakeId = "00000000-0000-0000-0000-000000000000";

        ResponseEntity<APIResponse<PatientDTO>> response = restTemplate.exchange(
                "/api/patients/" + fakeId, HttpMethod.PUT,
                new HttpEntity<>(validPayload()), SINGLE_REF);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void delete_returns204_andRemoves() {
        String id = createPatient(validPayload());

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/patients/" + id, HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(patientRepo.count()).isZero();
    }

    @Test
    void delete_returns404_forUnknownId() {
        String fakeId = "00000000-0000-0000-0000-000000000000";

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/patients/" + fakeId, HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }
}
