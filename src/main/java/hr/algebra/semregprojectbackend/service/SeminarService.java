package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional; // Sada se koristi direktno, ne u CompletableFuture

public interface SeminarService {

     // Vraća direktno List<SeminarDTO>
     List<SeminarDTO> getAllSeminars();

     // Vraća direktno Optional<SeminarDTO>
     Optional<SeminarDTO> save(@Valid SeminarUpdateCommand seminarUpdateCommand);

     // Vraća direktno SeminarDTO
     // Ovdje sam vratio Optional<SeminarDTO> jer je to fleksibilnije za 404 Not Found
     Optional<SeminarDTO> updateSeminar(Long id, SeminarDTO seminar);

     // Vraća void
     void deleteById(Long id);

     // Vraća direktno Optional<SeminarDTO>
     Optional<SeminarDTO> findSeminarByTopic(String topic);

}