package hr.algebra.semregprojectbackend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Registration implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Seminar seminar;

    private LocalDateTime registeredAt;


    public void setRegisteredAt(@NotNull LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
