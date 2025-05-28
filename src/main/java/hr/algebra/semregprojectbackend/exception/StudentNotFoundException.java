package hr.algebra.semregprojectbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StudentNotFoundException extends RuntimeException{
        public StudentNotFoundException(Long id) {
            super("Student with ID " + id + " not found.");
        }
    }

