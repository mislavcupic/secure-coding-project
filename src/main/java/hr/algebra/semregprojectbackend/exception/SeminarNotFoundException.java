package hr.algebra.semregprojectbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeminarNotFoundException extends RuntimeException {
    private final Long seminarId;

    public SeminarNotFoundException(Long seminarId) {
        super("Seminar not found with id: " + seminarId);
        this.seminarId = seminarId;
    }

}
