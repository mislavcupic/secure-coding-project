package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.service.SeminarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/seminars")
public class SeminarController {

    private final SeminarService seminarService;
    private final JmsTemplate jmsTemplate;

    public SeminarController(SeminarService seminarService, JmsTemplate jmsTemplate) {
        this.seminarService = seminarService;
        this.jmsTemplate = jmsTemplate;
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<SeminarDTO>>> getAllSeminars() {
        jmsTemplate.convertAndSend("seminar_queue", "Zahtjev za dohvat svih seminara!");
        return seminarService.getAllSeminars()
                .thenApply(ResponseEntity::ok) // mapiraj listu DTO-ova u ResponseEntity<List<SeminarDTO>>
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()); // Uhvati bilo koju iznimku i vrati 500
    }

    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<SeminarDTO>> findByTopicName(@RequestParam String topic) {
        return seminarService.findSeminarByTopic(topic)
                .thenApply(found -> found.map(ResponseEntity::ok) // mapiraj Optional<SeminarDTO> u ResponseEntity
                        .orElseGet(() -> ResponseEntity.notFound().build()))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<SeminarDTO>> createSeminar(@RequestBody @Valid SeminarUpdateCommand seminarUpdateCommand) {
        return seminarService.save(seminarUpdateCommand)
                .thenApply(savedSeminar -> savedSeminar
                        .map(ResponseEntity::ok) // mapiraj Optional<SeminarDTO> u ResponseEntity
                        .orElseGet(() -> ResponseEntity.badRequest().build())) // U slučaju neuspjeha kreiranja (npr. ako je id bio postavljen)
                .exceptionally(ex -> {
                    // Specifično rukovanje iznimkama
                    if (ex.getCause() instanceof IllegalArgumentException) {
                        return ResponseEntity.badRequest().build(); // Vrati 400 Bad Request za nevalidne argumente
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Generic 500 za ostale iznimke
                });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<SeminarDTO>> updateSeminar(@PathVariable Long id, @RequestBody SeminarDTO seminarDTO) {
        return seminarService.updateSeminar(id, seminarDTO)
                .thenApply(ResponseEntity::ok) // mapiraj SeminarDTO u ResponseEntity<SeminarDTO>
                .exceptionally(ex -> {
                    // Ako SeminarService baci RuntimeException za 'not found'
                    if (ex.getCause() != null && ex.getCause().getMessage().contains("not found")) {
                        return ResponseEntity.notFound().build(); // Vrati 404 Not Found
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content je standard za uspješno brisanje
    public CompletableFuture<Void> deleteSeminar(@PathVariable Long id) {
        return seminarService.deleteById(id)
                .exceptionally(ex -> {
                    // Ovdje možete specifičnije rukovati ako seminar ne postoji
                    // (npr. SeminarNotFoundException iz servisa)
                    throw new RuntimeException("Greška prilikom brisanja seminara: " + ex.getMessage());
                });
    }

}