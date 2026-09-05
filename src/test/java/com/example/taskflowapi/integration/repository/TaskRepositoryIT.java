package com.example.taskflowapi.integration.repository;

import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;
import com.example.taskflowapi.model.Task;
import com.example.taskflowapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryIT {

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        
        Task task1 = Task.builder()
                .title("Apprendre Spring Boot")
                .description("Lire la documentation sur Spring Data JPA")
                .priority(Priority.HIGH)
                .status(Status.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        Task task2 = Task.builder()
                .title("Créer des tests d'intégration")
                .description("Utiliser Testcontainers pour PostgreSQL")
                .priority(Priority.MEDIUM)
                .status(Status.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        taskRepository.saveAll(List.of(task1, task2));
    }

    @Test
    @DisplayName("IT - Recherche par mot-clé (titre ou description)")
    void searchByKeyword_shouldReturnMatchingTasks() {
        List<Task> result = taskRepository.searchByKeyword("Spring");
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Apprendre Spring Boot");
    }

    @Test
    @DisplayName("IT - Recherche par statut et priorité")
    void findByStatusAndPriority_shouldReturnMatchingTasks() {
        List<Task> result = taskRepository.findByStatusAndPriority(Status.TODO, Priority.MEDIUM);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Créer des tests d'intégration");
    }

    @Test
    @DisplayName("IT - Recherche par statut")
    void findByStatus_shouldReturnMatchingTasks() {
        List<Task> result = taskRepository.findByStatus(Status.IN_PROGRESS);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("IT - Recherche par priorité")
    void findByPriority_shouldReturnMatchingTasks() {
        List<Task> result = taskRepository.findByPriority(Priority.HIGH);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Apprendre Spring Boot");
    }
}
