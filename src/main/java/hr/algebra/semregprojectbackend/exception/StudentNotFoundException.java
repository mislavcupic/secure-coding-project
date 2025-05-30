package hr.algebra.semregprojectbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StudentNotFoundException extends RuntimeException{
    private final Long studentId;
        public StudentNotFoundException(Long studentId) {
            super("Student with ID " + studentId + " not found.");
            this.studentId = studentId;
        }
    }

