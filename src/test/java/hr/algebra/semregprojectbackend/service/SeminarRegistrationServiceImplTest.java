package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.RegistrationUpdateCommand;
import hr.algebra.semregprojectbackend.domain.Registration;
import hr.algebra.semregprojectbackend.domain.Seminar;
import hr.algebra.semregprojectbackend.domain.Student;
import hr.algebra.semregprojectbackend.dto.RegistrationDTO;
import hr.algebra.semregprojectbackend.repository.RegistrationRepository;
import hr.algebra.semregprojectbackend.repository.SeminarRepository;
import hr.algebra.semregprojectbackend.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class SeminarRegistrationServiceImplTest {

    private RegistrationRepository registrationRepository;
    private StudentRepository studentRepository;
    private SeminarRepository seminarRepository;
    private SeminarRegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        registrationRepository = mock(RegistrationRepository.class);
        studentRepository = mock(StudentRepository.class);
        seminarRepository = mock(SeminarRepository.class);

        registrationService = new SeminarRegistrationServiceImpl(
                registrationRepository, studentRepository, seminarRepository);
    }

    @Test
    void testGetAllRegistrations() {
        Student student = new Student(1L, "John Doe", "john@example.com");
        Seminar seminar = new Seminar(1L, "Topic1", "Lecturer1");
        Registration reg = new Registration(1L, student, seminar, LocalDateTime.now());

        given(registrationRepository.findAllWithStudentsAndSeminars()).willReturn(List.of(reg));

        List<RegistrationDTO> result = registrationService.getAllRegistrations();

        then(registrationRepository).should().findAllWithStudentsAndSeminars();
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getStudent().getName());
        assertEquals("Topic1", result.get(0).getSeminar().getTopic());
    }

    @Test
    void testGetRegistrationsByTopic_found() {
        String topic = "Java";

        Student student = new Student(1L, "Jane Doe", "jane@example.com");
        Seminar seminar = new Seminar(1L, topic, "Lecturer A");
        Registration reg = new Registration(1L, student, seminar, LocalDateTime.now());

        given(registrationRepository.findAllBySeminar_Topic(topic))
                .willReturn(Optional.of(List.of(reg)));

        Optional<List<RegistrationDTO>> resultOpt = registrationService.getRegistrationsByTopic(topic);

        then(registrationRepository).should().findAllBySeminar_Topic(topic);
        assertTrue(resultOpt.isPresent());
        List<RegistrationDTO> result = resultOpt.get();
        assertEquals(1, result.size());
        assertEquals(topic, result.get(0).getSeminar().getTopic());
    }

    @Test
    void testGetRegistrationsByStudentEmail_found() {
        String email = "student@example.com";

        Student student = new Student(1L, "Student Name", email);
        Seminar seminar = new Seminar(2L, "Spring Boot", "Lecturer B");
        Registration reg = new Registration(1L, student, seminar, LocalDateTime.now());

        given(registrationRepository.findAllByStudent_EmailWithStudents(email))
                .willReturn(Optional.of(List.of(reg)));

        Optional<List<RegistrationDTO>> resultOpt = registrationService.getRegistrationsByStudentEmail(email);

        then(registrationRepository).should().findAllByStudent_EmailWithStudents(email);
        assertTrue(resultOpt.isPresent());
        assertEquals(email, resultOpt.get().get(0).getStudent().getEmail());
    }

    @Test
    void testCreateRegistration_success() {
        RegistrationUpdateCommand cmd = new RegistrationUpdateCommand();
        cmd.setStudentId(1L);
        cmd.setSeminarId(2L);
        cmd.setRegisteredAt(LocalDateTime.now());

        Student student = new Student(1L, "Stu", "stu@example.com");
        Seminar seminar = new Seminar(2L, "Topic", "Lecturer");

        Registration savedRegistration = new Registration(10L, student, seminar, cmd.getRegisteredAt());

        given(studentRepository.findById(1L)).willReturn(Optional.of(student));
        given(seminarRepository.findById(2L)).willReturn(Optional.of(seminar));
        given(registrationRepository.save(any(Registration.class))).willReturn(savedRegistration);

        RegistrationDTO result = registrationService.createRegistration(cmd);

        then(studentRepository).should().findById(1L);
        then(seminarRepository).should().findById(2L);
        then(registrationRepository).should().save(any(Registration.class));

        assertEquals(10L, result.getId());
        assertEquals("Stu", result.getStudent().getName());
        assertEquals("Topic", result.getSeminar().getTopic());
        assertEquals(cmd.getRegisteredAt(), result.getRegisteredAt());
    }

    @Test
    void testUpdateRegistration_success() {
        Long regId = 5L;

        RegistrationUpdateCommand cmd = new RegistrationUpdateCommand();
        cmd.setStudentId(1L);
        cmd.setSeminarId(2L);
        cmd.setRegisteredAt(LocalDateTime.now());

        Student student = new Student(1L, "Stu", "stu@example.com");
        Seminar seminar = new Seminar(2L, "Topic", "Lecturer");
        Registration existingRegistration = new Registration(regId, student, seminar, LocalDateTime.now().minusDays(1));

        given(registrationRepository.findById(regId)).willReturn(Optional.of(existingRegistration));
        given(studentRepository.findById(1L)).willReturn(Optional.of(student));
        given(seminarRepository.findById(2L)).willReturn(Optional.of(seminar));
        given(registrationRepository.save(any(Registration.class))).willAnswer(invocation -> invocation.getArgument(0));

        RegistrationDTO result = registrationService.updateRegistration(regId, cmd);

        then(registrationRepository).should().findById(regId);
        then(studentRepository).should().findById(1L);
        then(seminarRepository).should().findById(2L);
        then(registrationRepository).should().save(existingRegistration);

        assertEquals(regId, result.getId());
        assertEquals("Stu", result.getStudent().getName());
        assertEquals("Topic", result.getSeminar().getTopic());
        assertEquals(cmd.getRegisteredAt(), result.getRegisteredAt());
    }

    @Test
    void testDeleteRegistration() {
        Long regId = 7L;

        willDoNothing().given(registrationRepository).deleteById(regId);

        registrationService.deleteRegistration(regId);

        then(registrationRepository).should().deleteById(regId);
    }

    @Test
    void testGetRegistrationsBySeminarId() {
        Long seminarId = 3L;
        Student student = new Student(1L, "Stu", "stu@example.com");
        Seminar seminar = new Seminar(seminarId, "Topic", "Lecturer");
        Registration reg = new Registration(1L, student, seminar, LocalDateTime.now());

        given(registrationRepository.findAllBySeminar_IdWithStudents(seminarId))
                .willReturn(Optional.of(List.of(reg)));

        Optional<List<RegistrationDTO>> resultOpt = registrationService.getRegistrationsBySeminarId(seminarId);

        then(registrationRepository).should().findAllBySeminar_IdWithStudents(seminarId);
        assertTrue(resultOpt.isPresent());
        assertEquals(1, resultOpt.get().size());
        assertEquals(seminarId, resultOpt.get().get(0).getSeminar().getId());
    }
}
