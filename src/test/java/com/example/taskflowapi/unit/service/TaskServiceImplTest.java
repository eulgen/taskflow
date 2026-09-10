package com.example.taskflowapi.unit.service;

import com.example.taskflowapi.dto.TaskRequestDTO;
import com.example.taskflowapi.dto.TaskResponseDTO;
import com.example.taskflowapi.exception.ResourceNotFoundException;
import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;
import com.example.taskflowapi.model.Task;
import com.example.taskflowapi.repository.TaskRepository;
import com.example.taskflowapi.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    // Fichier de test
    String awsAccessKey = "AKIAIOSFODNN7EXAMPLE";

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task sampleTask;
    private TaskRequestDTO sampleRequestDTO;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Tester le service")
                .description("Écrire des tests unitaires complets")
                .priority(Priority.HIGH)
                .status(Status.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequestDTO = TaskRequestDTO.builder()
                .title("Tester le service")
                .description("Écrire des tests unitaires complets")
                .priority(Priority.HIGH)
                .status(Status.TODO)
                .build();
    }


    @Test
    @DisplayName("createTask - doit sauvegarder et retourner le DTO de la tâche")
    void createTask_shouldSaveAndReturnTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDTO result = taskService.createTask(sampleRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Tester le service");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("getAllTasks - sans filtres doit retourner toutes les tâches")
    void getAllTasks_withoutFilters_shouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<TaskResponseDTO> results = taskService.getAllTasks(null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Tester le service");
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllTasks - avec filtre statut doit appeler findByStatus")
    void getAllTasks_withStatus_shouldFilterByStatus() {
        when(taskRepository.findByStatus(Status.TODO)).thenReturn(List.of(sampleTask));

        List<TaskResponseDTO> results = taskService.getAllTasks(Status.TODO, null, null);

        assertThat(results).hasSize(1);
        verify(taskRepository, times(1)).findByStatus(Status.TODO);
    }

    @Test
    @DisplayName("getAllTasks - avec filtre priorité doit appeler findByPriority")
    void getAllTasks_withPriority_shouldFilterByPriority() {
        when(taskRepository.findByPriority(Priority.HIGH)).thenReturn(List.of(sampleTask));

        List<TaskResponseDTO> results = taskService.getAllTasks(null, Priority.HIGH, null);

        assertThat(results).hasSize(1);
        verify(taskRepository, times(1)).findByPriority(Priority.HIGH);
    }

    @Test
    @DisplayName("getAllTasks - avec filtre statut et priorité")
    void getAllTasks_withStatusAndPriority_shouldFilterByBoth() {
        when(taskRepository.findByStatusAndPriority(Status.TODO, Priority.HIGH)).thenReturn(List.of(sampleTask));

        List<TaskResponseDTO> results = taskService.getAllTasks(Status.TODO, Priority.HIGH, null);

        assertThat(results).hasSize(1);
        verify(taskRepository, times(1)).findByStatusAndPriority(Status.TODO, Priority.HIGH);
    }

    @Test
    @DisplayName("getAllTasks - avec mot clé de recherche doit appeler searchByKeyword")
    void getAllTasks_withSearch_shouldSearchByKeyword() {
        when(taskRepository.searchByKeyword("Tester")).thenReturn(List.of(sampleTask));

        List<TaskResponseDTO> results = taskService.getAllTasks(null, null, "Tester");

        assertThat(results).hasSize(1);
        verify(taskRepository, times(1)).searchByKeyword("Tester");
    }

    @Test
    @DisplayName("getTaskById - doit retourner la tâche si elle existe")
    void getTaskById_whenExists_shouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponseDTO result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getTaskById - doit lever ResourceNotFoundException si introuvable")
    void getTaskById_whenNotExists_shouldThrowException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("updateTask - doit mettre à jour les champs et sauvegarder")
    void updateTask_shouldUpdateFieldsAndSave() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskRequestDTO updateDTO = TaskRequestDTO.builder()
                .title("Titre Modifié")
                .description("Description Modifiée")
                .priority(Priority.LOW)
                .status(Status.DONE)
                .build();

        TaskResponseDTO result = taskService.updateTask(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    @DisplayName("updateTaskStatus - doit mettre à jour uniquement le statut")
    void updateTaskStatus_shouldUpdateStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDTO result = taskService.updateTaskStatus(1L, Status.IN_PROGRESS);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    @DisplayName("updateTaskPriority - doit mettre à jour uniquement la priorité")
    void updateTaskPriority_shouldUpdatePriority() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDTO result = taskService.updateTaskPriority(1L, Priority.LOW);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    @DisplayName("deleteTask - doit supprimer la tâche si elle existe")
    void deleteTask_whenExists_shouldDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(sampleTask);
    }

    @Test
    @DisplayName("deleteTask - doit lever ResourceNotFoundException si introuvable")
    void deleteTask_whenNotExists_shouldThrowException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
