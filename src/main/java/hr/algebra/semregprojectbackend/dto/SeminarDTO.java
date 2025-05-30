package hr.algebra.semregprojectbackend.dto;


import hr.algebra.semregprojectbackend.domain.Seminar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeminarDTO {
    private Long id;
    private String topic;
    private String lecturer;

    public SeminarDTO(Seminar seminar) {
        this.id = seminar.getId();
        this.topic = seminar.getTopic();
        this.lecturer = seminar.getLecturer();
    }
}