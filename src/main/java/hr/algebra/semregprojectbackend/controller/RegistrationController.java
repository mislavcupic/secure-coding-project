package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.command.RegistrationUpdateCommand;
import hr.algebra.semregprojectbackend.dto.RegistrationDTO;
import hr.algebra.semregprojectbackend.service.SeminarRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final SeminarRegistrationService registrationService;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<RegistrationDTO> getAll() {
        return registrationService.getAllRegistrations();
    }

    @GetMapping("/by-topic/{topic}")
    public ResponseEntity<Optional<List<RegistrationDTO>>> getByTopic(@PathVariable String topic) {
        return ResponseEntity.ok(registrationService.getRegistrationsByTopic(topic));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<RegistrationDTO> create(@RequestBody @Valid RegistrationUpdateCommand registrationUpdateCommand) {
        return ResponseEntity.ok(registrationService.createRegistration(registrationUpdateCommand));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RegistrationDTO> update(@PathVariable Long id, @RequestBody RegistrationUpdateCommand registrationUpdateCommand) {
        return ResponseEntity.ok(registrationService.updateRegistration(id, registrationUpdateCommand));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/by-seminar/{seminarId}")
    public ResponseEntity<Optional<List<RegistrationDTO>>> getBySeminar(@PathVariable Long seminarId) {
        return ResponseEntity.ok(registrationService.getRegistrationsBySeminarId(seminarId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-student/{email}")
    public ResponseEntity<Optional<List<RegistrationDTO>>> getByStudent(@PathVariable String email) {
        return ResponseEntity.ok(registrationService.getRegistrationsByStudentEmail(email));
    }
}
