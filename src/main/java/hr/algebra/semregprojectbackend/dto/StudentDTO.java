package hr.algebra.semregprojectbackend.dto;

import hr.algebra.semregprojectbackend.domain.Student;

public class StudentDTO {
    private Long id;

    private String name;
    private String email;

    public StudentDTO(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.email = student.getEmail();

    }
public StudentDTO(){}
    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }


}
