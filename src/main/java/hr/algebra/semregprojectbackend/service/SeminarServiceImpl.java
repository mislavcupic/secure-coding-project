package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.domain.Seminar;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.exception.SeminarNotFoundException;
import hr.algebra.semregprojectbackend.repository.SeminarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeminarServiceImpl implements SeminarService {
    private final SeminarRepository seminarRepo;

    public SeminarServiceImpl(SeminarRepository seminarRepo) {
        this.seminarRepo = seminarRepo;
    }

    @Override
    public Optional<SeminarDTO> findSeminarByTopic(String topic) {

        Optional<Seminar> seminarOptional = seminarRepo.findByTopicIgnoreCase(topic);
        return seminarOptional.map(SeminarDTO::new);
    }

    @Override
    public List<SeminarDTO> getAllSeminars() {
        return seminarRepo.findAll().stream().map(SeminarDTO::new).toList();
    }


    @Override
    public Optional<SeminarDTO> save(SeminarUpdateCommand seminarUpdateCommand) {

        Seminar seminar = new Seminar();
        seminar.setTopic(seminarUpdateCommand.getTopic());
        seminar.setLecturer(seminarUpdateCommand.getLecturer());

        Seminar savedSeminar = seminarRepo.save(seminar);
        return Optional.of(new SeminarDTO(savedSeminar));
    }


    @Override
    public Optional<SeminarDTO> updateSeminar(Long id, SeminarDTO seminarDTO) {
        return seminarRepo.findById(id)
                .map(seminar -> {
                    seminar.setTopic(seminarDTO.getTopic()); //  Nema sanitizacije
                    seminar.setLecturer(seminarDTO.getLecturer());
                    Seminar updated = seminarRepo.save(seminar);
                    return new SeminarDTO(updated);
                });
    }


    @Override
    public void deleteById(Long id) {
        if (!seminarRepo.existsById(id)) {
            throw new SeminarNotFoundException(id);
        }
        seminarRepo.deleteById(id);
    }
}