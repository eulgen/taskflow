package com.example.taskflowapi.integration.service;

import com.example.taskflowapi.dto.TaskRequestDTO;
import com.example.taskflowapi.dto.TaskResponseDTO;
import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;
import com.example.taskflowapi.repository.TaskRepository;
import com.example.taskflowapi.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rollback automatically after each test
class TaskServiceIT {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("IT - Création et sauvegarde d'une tâche via le service")
    void createTask_shouldPersistInDatabase() {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Tâche de Service IT")
                .description("Description de test pour le service")
                .priority(Priority.LOW)
                .status(Status.TODO)
                .build();

        TaskResponseDTO createdTask = taskService.createTask(request);

        assertThat(createdTask.getId()).isNotNull();
        assertThat(taskRepository.findById(createdTask.getId())).isPresent();
        assertThat(taskRepository.findById(createdTask.getId()).get().getTitle()).isEqualTo("Tâche de Service IT");
    }

    @Test
    @DisplayName("IT - Mise à jour du statut modifie la base de données")
    void updateTaskStatus_shouldModifyDatabase() {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Mise à jour statut")
                .description("Test")
                .priority(Priority.LOW)
                .status(Status.TODO)
                .build();
        TaskResponseDTO createdTask = taskService.createTask(request);

        TaskResponseDTO updatedTask = taskService.updateTaskStatus(createdTask.getId(), Status.DONE);

        assertThat(updatedTask.getStatus()).isEqualTo(Status.DONE);
        assertThat(taskRepository.findById(createdTask.getId()).get().getStatus()).isEqualTo(Status.DONE);
    }
}
