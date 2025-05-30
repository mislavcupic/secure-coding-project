package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.domain.Seminar;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.exception.SeminarNotFoundException;
import hr.algebra.semregprojectbackend.repository.SeminarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class SeminarServiceImplTest {

    @Mock
    private SeminarRepository seminarRepo;

    @InjectMocks
    private SeminarServiceImpl seminarService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Seminar sampleSeminar() {
        return new Seminar(1L, "Spring Boot Basics", "John Smith");
    }



    @Test
    void findSeminarByTopic_whenFound_returnsSeminarDTO() {
        // given
        String topic = "Spring Boot Basics";
        given(seminarRepo.findByTopicIgnoreCase(topic)).willReturn(Optional.of(sampleSeminar()));

        // when
        Optional<SeminarDTO> result = seminarService.findSeminarByTopic(topic);

        // then
        then(seminarRepo).should().findByTopicIgnoreCase(topic);
        assertTrue(result.isPresent());
        assertEquals(topic, result.get().getTopic());
    }

    @Test
    void findSeminarByTopic_whenNotFound_returnsEmpty() {
        // given
        String topic = "Unknown Topic";
        given(seminarRepo.findByTopicIgnoreCase(topic)).willReturn(Optional.empty());

        // when
        Optional<SeminarDTO> result = seminarService.findSeminarByTopic(topic);

        // then
        then(seminarRepo).should().findByTopicIgnoreCase(topic);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllSeminars_returnsListOfSeminarDTO() {
        // given
        List<Seminar> seminars = List.of(sampleSeminar());
        given(seminarRepo.findAll()).willReturn(seminars);

        // when
        List<SeminarDTO> result = seminarService.getAllSeminars();

        // then
        then(seminarRepo).should().findAll();
        assertEquals(1, result.size());
        assertEquals("Spring Boot Basics", result.get(0).getTopic());
    }

    @Test
    void save_createsAndReturnsSeminarDTO() {
        // given
        SeminarUpdateCommand command = new SeminarUpdateCommand();
        command.setTopic("New Topic");
        command.setLecturer("New Lecturer");

        Seminar seminarToSave = new Seminar();
        seminarToSave.setTopic(command.getTopic());
        seminarToSave.setLecturer(command.getLecturer());

        Seminar savedSeminar = new Seminar(1L, command.getTopic(), command.getLecturer());
        given(seminarRepo.save(any(Seminar.class))).willReturn(savedSeminar);

        // when
        Optional<SeminarDTO> result = seminarService.save(command);

        // then
        then(seminarRepo).should().save(any(Seminar.class));
        assertTrue(result.isPresent());
        assertEquals(command.getTopic(), result.get().getTopic());
    }

    @Test
    void updateSeminar_whenFound_updatesAndReturnsDTO() {
        // given
        Long id = 1L;
        Seminar existingSeminar = sampleSeminar();

        SeminarDTO updateDto = new SeminarDTO();
        updateDto.setTopic("Updated Topic");
        updateDto.setLecturer("Updated Lecturer");

        Seminar updatedSeminar = new Seminar(id, updateDto.getTopic(), updateDto.getLecturer());

        given(seminarRepo.findById(id)).willReturn(Optional.of(existingSeminar));
        given(seminarRepo.save(existingSeminar)).willReturn(updatedSeminar);

        // when
        Optional<SeminarDTO> result = seminarService.updateSeminar(id, updateDto);

        // then
        then(seminarRepo).should().findById(id);
        then(seminarRepo).should().save(existingSeminar);

        assertTrue(result.isPresent());
        assertEquals("Updated Topic", result.get().getTopic());
        assertEquals("Updated Lecturer", result.get().getLecturer());
    }

    @Test
    void updateSeminar_whenNotFound_returnsEmpty() {
        // given
        Long id = 99L;
        SeminarDTO updateDto = new SeminarDTO();
        given(seminarRepo.findById(id)).willReturn(Optional.empty());

        // when
        Optional<SeminarDTO> result = seminarService.updateSeminar(id, updateDto);

        // then
        then(seminarRepo).should().findById(id);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_whenExists_deletes() {
        // given
        Long id = 1L;
        given(seminarRepo.existsById(id)).willReturn(true);
        willDoNothing().given(seminarRepo).deleteById(id);

        // when
        seminarService.deleteById(id);

        // then
        then(seminarRepo).should().existsById(id);
        then(seminarRepo).should().deleteById(id);
    }

    @Test
    void deleteById_whenNotExists_throwsException() {
        // given
        Long id = 99L;
        given(seminarRepo.existsById(id)).willReturn(false);

        // when + then
        SeminarNotFoundException ex = assertThrows(SeminarNotFoundException.class, () -> seminarService.deleteById(id));
        assertEquals(id, ex.getSeminarId());
        then(seminarRepo).should().existsById(id);
        then(seminarRepo).should(never()).deleteById(anyLong());
    }
}

