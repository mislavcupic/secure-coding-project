package hr.algebra.semregprojectbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeminarNotFoundException extends RuntimeException {

    public SeminarNotFoundException(Long id) {
        super("Seminar with ID " + id + " not found.");
    }
}