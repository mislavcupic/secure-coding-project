package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;

import hr.algebra.semregprojectbackend.service.SeminarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Dodano za Optional
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/seminars")
public class SeminarController {
    private static final Logger logger = Logger.getLogger(SeminarController.class.getName());
    private final SeminarService seminarService;

    public SeminarController(SeminarService seminarService) {
        this.seminarService = seminarService;

    }

       @GetMapping
    public ResponseEntity<List<SeminarDTO>> getAllSeminars() {
        try {
            List<SeminarDTO> seminars = seminarService.getAllSeminars();
            return ResponseEntity.ok(seminars);
        } catch (Exception ex) {
            logger.warning("Error occurred while fetching seminars"+ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<SeminarDTO> findByTopicName(@RequestParam String topic) {
        try {

            Optional<SeminarDTO> found = seminarService.findSeminarByTopic(topic);
            return found.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception ex) {
            logger.warning("Error occurred while searching seminars by topic: "+ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<SeminarDTO> createSeminar(@RequestBody @Valid SeminarUpdateCommand seminarUpdateCommand) {
        try {

            Optional<SeminarDTO> savedSeminar = seminarService.save(seminarUpdateCommand);
            return savedSeminar.map(s -> ResponseEntity.status(HttpStatus.CREATED).body(s))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            logger.warning("Error occurred while creating seminar: "+ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeminarDTO> updateSeminar(@PathVariable Long id, @RequestBody SeminarDTO seminarDTO) {
        try {
            // Ovdje je Optional također dobar za signaliziranje je li entitet pronađen za ažuriranje
            Optional<SeminarDTO> updatedSeminar = seminarService.updateSeminar(id, seminarDTO);
            return updatedSeminar.map(ResponseEntity::ok) // 200 OK ako je ažurirano
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // 404 Not Found ako nije pronađeno
        } catch (Exception ex) {
            logger.warning("Error occurred while updating seminar: "+ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Ovo je i dalje valjana anotacija za sinkrone metode
    public void deleteSeminar(@PathVariable Long id) {

        seminarService.deleteById(id);
    }
}

