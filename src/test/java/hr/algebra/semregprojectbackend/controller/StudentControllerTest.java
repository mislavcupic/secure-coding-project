package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.command.StudentUpdateCommand;
import hr.algebra.semregprojectbackend.dto.StudentDTO;
import hr.algebra.semregprojectbackend.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllStudents_returnsListOfStudents() {
        // given
        List<StudentDTO> students = List.of(
                new StudentDTO(1L, "John Doe", "john@example.com"),
                new StudentDTO(2L, "Jane Roe", "jane@example.com")
        );
        given(studentService.getAllStudents()).willReturn(students);

        // when
        ResponseEntity<List<StudentDTO>> response = studentController.getAllStudents();

        // then
        then(studentService).should().getAllStudents();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(students, response.getBody());
    }

    @Test
    void getStudentByEmail_existingEmail_returnsStudent() {
        // given
        String email = "john@example.com";
        StudentDTO studentDTO = new StudentDTO(1L, "John Doe", email);
        given(studentService.getStudentByEmail(email)).willReturn(Optional.of(studentDTO));

        // when
        ResponseEntity<StudentDTO> response = studentController.getStudentByEmail(email);

        // then
        then(studentService).should().getStudentByEmail(email);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(studentDTO, response.getBody());
    }

    @Test
    void getStudentByEmail_nonExistingEmail_returnsNotFound() {
        // given
        String email = "unknown@example.com";
        given(studentService.getStudentByEmail(email)).willReturn(Optional.empty());

        // when
        ResponseEntity<StudentDTO> response = studentController.getStudentByEmail(email);

        // then
        then(studentService).should().getStudentByEmail(email);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createStudent_validCommand_returnsCreatedStudent() {
        // given
        StudentUpdateCommand command = new StudentUpdateCommand();
        command.setName("John Doe");
        command.setEmail("john@example.com");

        StudentDTO createdStudent = new StudentDTO(1L, "John Doe", "john@example.com");
        given(studentService.save(command)).willReturn(Optional.of(createdStudent));

        // when
        ResponseEntity<StudentDTO> response = studentController.createStudent(command);

        // then
        then(studentService).should().save(command);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdStudent, response.getBody());
    }

    @Test
    void createStudent_invalidCommand_returnsBadRequest() {
        // given
        StudentUpdateCommand command = new StudentUpdateCommand();
        given(studentService.save(command)).willReturn(Optional.empty());

        // when
        ResponseEntity<StudentDTO> response = studentController.createStudent(command);

        // then
        then(studentService).should().save(command);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void updateStudent_existingStudent_returnsUpdatedStudent() {
        // given
        Long id = 1L;
        StudentUpdateCommand command = new StudentUpdateCommand();
        command.setName("Updated Name");
        command.setEmail("updated@example.com");

        StudentDTO updatedStudent = new StudentDTO(id, "Updated Name", "updated@example.com");
        given(studentService.updateStudent(id, command)).willReturn(Optional.of(updatedStudent));

        // when
        ResponseEntity<Optional<StudentDTO>> response = studentController.updateStudent(id, command);

        // then
        then(studentService).should().updateStudent(id, command);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isPresent());
        assertEquals(updatedStudent, response.getBody().get());
    }

    @Test
    void updateStudent_nonExistingStudent_returnsEmptyOptional() {
        // given
        Long id = 99L;
        StudentUpdateCommand command = new StudentUpdateCommand();
        given(studentService.updateStudent(id, command)).willReturn(Optional.empty());

        // when
        ResponseEntity<Optional<StudentDTO>> response = studentController.updateStudent(id, command);

        // then
        then(studentService).should().updateStudent(id, command);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void deleteStudent_callsServiceDelete() {
        // given
        Long id = 1L;
        willDoNothing().given(studentService).deleteById(id);

        // when
        studentController.deleteStudent(id);

        // then
        then(studentService).should().deleteById(id);
    }
}

