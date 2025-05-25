package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.domain.Seminar;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.repository.SeminarRepository;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async; // Dodano za asinkrono izvršavanje

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture; // Dodano za asinkrone operacije

@Service
@Async // Omogućava da metode unutar ovog servisa budu asinkrone
public class SeminarServiceImpl implements SeminarService {

    private final SeminarRepository seminarRepo;

    public SeminarServiceImpl(SeminarRepository seminarRepo) {
        this.seminarRepo = seminarRepo;
    }

    @Override
    public CompletableFuture<List<SeminarDTO>> getAllSeminars() {
        return CompletableFuture.supplyAsync(() ->
                seminarRepo.findAll().stream().map(SeminarDTO::new).toList()
        );
    }

    @Override
    public CompletableFuture<Optional<SeminarDTO>> save(SeminarUpdateCommand seminarUpdateCommand) {
        return CompletableFuture.supplyAsync(() -> {
            if (seminarUpdateCommand.getId() != null) {
                throw new IllegalArgumentException("ID ne smije biti postavljen kod kreiranja novog seminara.");
            }

            Seminar seminar = new Seminar();
            seminar.setTopic(seminarUpdateCommand.getTopic());
            seminar.setLecturer(seminarUpdateCommand.getLecturer());

            Seminar savedSeminar = seminarRepo.save(seminar);
            return Optional.of(new SeminarDTO(savedSeminar));
        });
    }

    @Override
    public CompletableFuture<SeminarDTO> updateSeminar(Long id, SeminarDTO seminarDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Seminar> seminarOpt = seminarRepo.findById(id);
            if (seminarOpt.isPresent()) {
                Seminar seminar = seminarOpt.get();
                seminar.setTopic(seminarDTO.getTopic());
                seminar.setLecturer(seminarDTO.getLecturer());
                Seminar updated = seminarRepo.save(seminar);
                return new SeminarDTO(updated);
            } else {
                // Važno: Ovdje je bolja praksa baciti specifičnu iznimku
                // koju kontroler može uhvatiti i pretvoriti u HTTP 404 Not Found.
                throw new RuntimeException("Seminar not found with id: " + id);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteById(Long id) {
        return CompletableFuture.runAsync(() ->
                seminarRepo.deleteById(id)
        );
    }

    @Override
    public CompletableFuture<Optional<SeminarDTO>> findSeminarByTopic(String topic) {
        return CompletableFuture.supplyAsync(() ->
                seminarRepo.findByTopicIgnoreCase(topic.trim()).map(SeminarDTO::new)
        );
    }
}