package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.StudentUpdateCommand;
import hr.algebra.semregprojectbackend.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {
   List<StudentDTO> getAllStudents();
   Optional<StudentDTO> save(StudentUpdateCommand studentUpdateCommand);
   Optional<StudentDTO> updateStudent(Long id, StudentUpdateCommand studentUpdateCommand);
   void deleteById(Long id);
   Optional<StudentDTO> getStudentByEmail(String email);

}
