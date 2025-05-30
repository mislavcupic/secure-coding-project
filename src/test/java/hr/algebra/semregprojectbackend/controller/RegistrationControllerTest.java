package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.command.RegistrationUpdateCommand;
import hr.algebra.semregprojectbackend.dto.RegistrationDTO;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.dto.StudentDTO;
import hr.algebra.semregprojectbackend.service.SeminarRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationControllerTest {

    @Mock
    private SeminarRegistrationService registrationService;

    @InjectMocks
    private RegistrationController registrationController;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
    }

    private RegistrationDTO sampleRegistrationDTO() {
        StudentDTO student = new StudentDTO(1L, "John Doe", "john@example.com");
        SeminarDTO seminar = new SeminarDTO(1L, "Java Basics", "Jane Smith");
        return new RegistrationDTO(1L, student, seminar, LocalDateTime.now());
    }

    @Test
    void getAll_returnsAllRegistrations() {
        // given
        List<RegistrationDTO> registrations = List.of(sampleRegistrationDTO());
        given(registrationService.getAllRegistrations()).willReturn(registrations);

        // when
        List<RegistrationDTO> result = registrationController.getAll();

        // then
        then(registrationService).should().getAllRegistrations();
        assertEquals(registrations, result);
    }

    @Test
    void getByTopic_existingTopic_returnsRegistrations() {
        // given
        String topic = "Java Basics";
        List<RegistrationDTO> registrations = List.of(sampleRegistrationDTO());
        given(registrationService.getRegistrationsByTopic(topic)).willReturn(Optional.of(registrations));

        // when
        ResponseEntity<Optional<List<RegistrationDTO>>> response = registrationController.getByTopic(topic);

        // then
        then(registrationService).should().getRegistrationsByTopic(topic);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals(registrations, response.getBody().get());
    }

    @Test
    void create_returnsCreatedRegistration() {
        // given
        RegistrationUpdateCommand command = new RegistrationUpdateCommand();
        RegistrationDTO created = sampleRegistrationDTO();
        given(registrationService.createRegistration(command)).willReturn(created);

        // when
        ResponseEntity<RegistrationDTO> response = registrationController.create(command);

        // then
        then(registrationService).should().createRegistration(command);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(created, response.getBody());
    }

    @Test
    void update_returnsUpdatedRegistration() {
        // given
        Long id = 1L;
        RegistrationUpdateCommand command = new RegistrationUpdateCommand();
        RegistrationDTO updated = sampleRegistrationDTO();
        given(registrationService.updateRegistration(id, command)).willReturn(updated);

        // when
        ResponseEntity<RegistrationDTO> response = registrationController.update(id, command);

        // then
        then(registrationService).should().updateRegistration(id, command);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void delete_callsServiceDelete() {
        // given
        Long id = 1L;
        willDoNothing().given(registrationService).deleteRegistration(id);

        // when
        ResponseEntity<Void> response = registrationController.delete(id);

        // then
        then(registrationService).should().deleteRegistration(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getBySeminar_existingSeminar_returnsRegistrations() {
        // given
        Long seminarId = 1L;
        List<RegistrationDTO> registrations = List.of(sampleRegistrationDTO());
        given(registrationService.getRegistrationsBySeminarId(seminarId)).willReturn(Optional.of(registrations));

        // when
        ResponseEntity<Optional<List<RegistrationDTO>>> response = registrationController.getBySeminar(seminarId);

        // then
        then(registrationService).should().getRegistrationsBySeminarId(seminarId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals(registrations, response.getBody().get());
    }

    @Test
    void getByStudent_existingEmail_returnsRegistrations() {
        // given
        String email = "john@example.com";
        List<RegistrationDTO> registrations = List.of(sampleRegistrationDTO());
        given(registrationService.getRegistrationsByStudentEmail(email)).willReturn(Optional.of(registrations));

        // when
        ResponseEntity<Optional<List<RegistrationDTO>>> response = registrationController.getByStudent(email);

        // then
        then(registrationService).should().getRegistrationsByStudentEmail(email);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals(registrations, response.getBody().get());
    }
}

