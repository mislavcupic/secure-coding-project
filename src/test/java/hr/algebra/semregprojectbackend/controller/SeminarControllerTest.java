package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.service.SeminarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeminarControllerTest {

   private SeminarController seminarController;

   @Mock
   private SeminarService seminarService;

   @BeforeEach
   void setUp() {
      MockitoAnnotations.openMocks(this);
      seminarController = new SeminarController(seminarService);
   }

   @Test
   void testGetAllSeminars_Success() {
      SeminarDTO seminar = new SeminarDTO(1L, "Topic1", "Lecturer1");
      when(seminarService.getAllSeminars()).thenReturn(List.of(seminar));

      ResponseEntity<List<SeminarDTO>> response = seminarController.getAllSeminars();

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertFalse(response.getBody().isEmpty());
      assertEquals("Topic1", response.getBody().get(0).getTopic());
   }

   @Test
   void testGetAllSeminars_Exception() {
      when(seminarService.getAllSeminars()).thenThrow(new RuntimeException("DB failure"));

      ResponseEntity<List<SeminarDTO>> response = seminarController.getAllSeminars();

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNull(response.getBody());
   }

   @Test
   void testFindByTopicName_Found() {
      SeminarDTO seminar = new SeminarDTO(2L, "Java", "John");
      when(seminarService.findSeminarByTopic("Java")).thenReturn(Optional.of(seminar));

      ResponseEntity<SeminarDTO> response = seminarController.findByTopicName("Java");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("John", response.getBody().getLecturer());
   }

   @Test
   void testFindByTopicName_NotFound() {
      when(seminarService.findSeminarByTopic("Unknown")).thenReturn(Optional.empty());

      ResponseEntity<SeminarDTO> response = seminarController.findByTopicName("Unknown");

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertNull(response.getBody());
   }

   @Test
   void testFindByTopicName_Exception() {
      when(seminarService.findSeminarByTopic(anyString())).thenThrow(new RuntimeException("DB error"));

      ResponseEntity<SeminarDTO> response = seminarController.findByTopicName("AnyTopic");

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNull(response.getBody());
   }

   @Test
   void testCreateSeminar_Success() {
      SeminarUpdateCommand cmd = new SeminarUpdateCommand();

      SeminarDTO savedSeminar = new SeminarDTO(3L, "New Topic", "New Lecturer");

      when(seminarService.save(cmd)).thenReturn(Optional.of(savedSeminar));

      ResponseEntity<SeminarDTO> response = seminarController.createSeminar(cmd);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(3L, response.getBody().getId());
   }

   @Test
   void testCreateSeminar_BadRequest() {
      SeminarUpdateCommand cmd = new SeminarUpdateCommand();

      when(seminarService.save(cmd)).thenReturn(Optional.empty());

      ResponseEntity<SeminarDTO> response = seminarController.createSeminar(cmd);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNull(response.getBody());
   }

   @Test
   void testCreateSeminar_IllegalArgumentException() {
      SeminarUpdateCommand cmd = new SeminarUpdateCommand();

      when(seminarService.save(cmd)).thenThrow(new IllegalArgumentException("Invalid data"));

      ResponseEntity<SeminarDTO> response = seminarController.createSeminar(cmd);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNull(response.getBody());
   }

   @Test
   void testCreateSeminar_Exception() {
      SeminarUpdateCommand cmd = new SeminarUpdateCommand();

      when(seminarService.save(cmd)).thenThrow(new RuntimeException("DB error"));

      ResponseEntity<SeminarDTO> response = seminarController.createSeminar(cmd);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNull(response.getBody());
   }

   @Test
   void testUpdateSeminar_Found() {
      Long id = 1L;
      SeminarDTO seminarDTO = new SeminarDTO(null, "Updated Topic", "Updated Lecturer");
      SeminarDTO updatedSeminar = new SeminarDTO(id, "Updated Topic", "Updated Lecturer");

      when(seminarService.updateSeminar(id, seminarDTO)).thenReturn(Optional.of(updatedSeminar));

      ResponseEntity<SeminarDTO> response = seminarController.updateSeminar(id, seminarDTO);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Updated Topic", response.getBody().getTopic());
   }

   @Test
   void testUpdateSeminar_NotFound() {
      Long id = 99L;
      SeminarDTO seminarDTO = new SeminarDTO(null, "Updated Topic", "Updated Lecturer");

      when(seminarService.updateSeminar(id, seminarDTO)).thenReturn(Optional.empty());

      ResponseEntity<SeminarDTO> response = seminarController.updateSeminar(id, seminarDTO);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
      assertNull(response.getBody());
   }

   @Test
   void testUpdateSeminar_Exception() {
      Long id = 1L;
      SeminarDTO seminarDTO = new SeminarDTO(null, "Updated Topic", "Updated Lecturer");

      when(seminarService.updateSeminar(id, seminarDTO)).thenThrow(new RuntimeException("DB error"));

      ResponseEntity<SeminarDTO> response = seminarController.updateSeminar(id, seminarDTO);

      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNull(response.getBody());
   }

   @Test
   void testDeleteSeminar() {
      Long id = 1L;

      // metoda deleteById nema povratnu vrijednost, pa samo verifikacija da se pozvala
      doNothing().when(seminarService).deleteById(id);

      seminarController.deleteSeminar(id);

      verify(seminarService, times(1)).deleteById(id);
   }
}
