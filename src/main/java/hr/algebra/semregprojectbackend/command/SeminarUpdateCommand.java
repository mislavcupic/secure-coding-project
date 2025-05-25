package hr.algebra.semregprojectbackend.command;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import org.springframework.context.annotation.Primary;

@Data
public class SeminarUpdateCommand {


    private Long id;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Lecturer is required")
    private String lecturer;

    public SeminarUpdateCommand(String topic, String lecturer) {
        this.topic = topic;
        this.lecturer = lecturer;
    }

    @Primary

    public Long getId() {
        return id;
    }
}
