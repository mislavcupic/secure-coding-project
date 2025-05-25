package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture; // Dodano za asinkrone operacije

public interface SeminarService {

     // Promijenjeno: Vraća CompletableFuture<List<SeminarDTO>>
     CompletableFuture<List<SeminarDTO>> getAllSeminars();

     // Promijenjeno: Vraća CompletableFuture<Optional<SeminarDTO>>
     CompletableFuture<Optional<SeminarDTO>> save(@Valid SeminarUpdateCommand seminarUpdateCommand);

     // Promijenjeno: Vraća CompletableFuture<SeminarDTO>
     CompletableFuture<SeminarDTO> updateSeminar(Long id, SeminarDTO seminar);

     // Promijenjeno: Vraća CompletableFuture<Void> jer nema povratne vrijednosti
     CompletableFuture<Void> deleteById(Long id);

     // Promijenjeno: Vraća CompletableFuture<Optional<SeminarDTO>>
     CompletableFuture<Optional<SeminarDTO>> findSeminarByTopic(String topic);

}