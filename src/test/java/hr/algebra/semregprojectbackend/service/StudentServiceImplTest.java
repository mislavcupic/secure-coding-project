package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.StudentUpdateCommand;
import hr.algebra.semregprojectbackend.domain.Student;
import hr.algebra.semregprojectbackend.dto.StudentDTO;
import hr.algebra.semregprojectbackend.exception.StudentNotFoundException;
import hr.algebra.semregprojectbackend.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

class
StudentServiceImplTest {

    private StudentRepository studentRepo;
    private StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        studentRepo = mock(StudentRepository.class);
        studentService = new StudentServiceImpl(studentRepo);
    }

    @Test
    void testGetAllStudents() {
        // given
        Student s1 = new Student(1L, "John Doe", "john@example.com");
        Student s2 = new Student(2L, "Jane Roe", "jane@example.com");
        given(studentRepo.findAll()).willReturn(List.of(s1, s2));

        // when
        List<StudentDTO> allStudents = studentService.getAllStudents();

        // then
        then(studentRepo).should().findAll();
        assertEquals(2, allStudents.size());
        assertEquals("John Doe", allStudents.get(0).getName());
    }

    @Test
    void testGetStudentByEmail_found() {
        // given
        String email = "test@example.com";
        Student student = new Student(1L, "Test Name", email);
        given(studentRepo.findByEmailIgnoreCase(email)).willReturn(Optional.of(student));

        // when
        Optional<StudentDTO> studentOpt = studentService.getStudentByEmail(email);

        // then
        then(studentRepo).should().findByEmailIgnoreCase(email);
        assertTrue(studentOpt.isPresent());
        assertEquals("Test Name", studentOpt.get().getName());
    }

    @Test
    void testGetStudentByEmail_notFound() {
        // given
        String email = "notfound@example.com";
        given(studentRepo.findByEmailIgnoreCase(email)).willReturn(Optional.empty());

        // when
        Optional<StudentDTO> studentOpt = studentService.getStudentByEmail(email);

        // then
        then(studentRepo).should().findByEmailIgnoreCase(email);
        assertTrue(studentOpt.isEmpty());
    }

    @Test
    void testSaveStudent() {
        // given
        StudentUpdateCommand cmd = new StudentUpdateCommand();
        cmd.setName("New Student");
        cmd.setEmail("new@example.com");

        Student savedStudent = new Student(1L, cmd.getName(), cmd.getEmail());
        given(studentRepo.save(any(Student.class))).willReturn(savedStudent);

        // when
        Optional<StudentDTO> result = studentService.save(cmd);

        // then
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        then(studentRepo).should().save(captor.capture());
        Student capturedStudent = captor.getValue();
        assertEquals(cmd.getName(), capturedStudent.getName());
        assertEquals(cmd.getEmail(), capturedStudent.getEmail());

        assertTrue(result.isPresent());
        assertEquals(savedStudent.getId(), result.get().getId());
    }

    @Test
    void testUpdateStudent_existing() {
        // given
        Long id = 1L;
        Student existing = new Student(id, "Old Name", "old@example.com");
        StudentUpdateCommand cmd = new StudentUpdateCommand();
        cmd.setName("Updated Name");
        cmd.setEmail("updated@example.com");

        given(studentRepo.findById(id)).willReturn(Optional.of(existing));
        given(studentRepo.save(any(Student.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Optional<StudentDTO> updatedOpt = studentService.updateStudent(id, cmd);

        // then
        then(studentRepo).should().findById(id);
        then(studentRepo).should().save(existing);
        assertTrue(updatedOpt.isPresent());
        assertEquals(cmd.getName(), updatedOpt.get().getName());
        assertEquals(cmd.getEmail(), updatedOpt.get().getEmail());
    }

    @Test
    void testUpdateStudent_notFound() {
        // given
        Long id = 1L;
        StudentUpdateCommand cmd = new StudentUpdateCommand();
        given(studentRepo.findById(id)).willReturn(Optional.empty());

        // when + then
        StudentNotFoundException ex = assertThrows(StudentNotFoundException.class,
                () -> studentService.updateStudent(id, cmd));

        then(studentRepo).should().findById(id);
        then(studentRepo).should(never()).save(any());
        assertEquals(id, ex.getStudentId()); // Pretpostavljam da ima getStudentId() u tvojoj exception klasi
    }

    @Test
    void testDeleteById_existing() {
        // given
        Long id = 1L;
        Student existing = new Student(id, "Name", "email@example.com");
        given(studentRepo.findById(id)).willReturn(Optional.of(existing));

        // when
        studentService.deleteById(id);

        // then
        then(studentRepo).should().findById(id);
        then(studentRepo).should().deleteById(id);
    }

    @Test
    void testDeleteById_notFound() {
        // given
        Long id = 1L;
        given(studentRepo.findById(id)).willReturn(Optional.empty());

        // when + then
        StudentNotFoundException ex = assertThrows(StudentNotFoundException.class,
                () -> studentService.deleteById(id));

        then(studentRepo).should().findById(id);
        then(studentRepo).should(never()).deleteById(anyLong());
        assertEquals(id, ex.getStudentId()); // opet pretpostavka za getter
    }
}

