package com.example.taskflowapi.unit.controller;

import com.example.taskflowapi.controller.TaskController;
import com.example.taskflowapi.dto.TaskRequestDTO;
import com.example.taskflowapi.dto.TaskResponseDTO;
import com.example.taskflowapi.exception.ResourceNotFoundException;
import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;
import com.example.taskflowapi.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskResponseDTO sampleResponseDTO;
    private TaskRequestDTO sampleRequestDTO;

    @BeforeEach
    void setUp() {
        sampleResponseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Tâche de test")
                .description("Description de test")
                .priority(Priority.HIGH)
                .status(Status.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequestDTO = TaskRequestDTO.builder()
                .title("Tâche de test")
                .description("Description de test")
                .priority(Priority.HIGH)
                .status(Status.TODO)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Succès de création (201 Created)")
    void createTask_shouldReturn201Created() throws Exception {
        when(taskService.createTask(any(TaskRequestDTO.class))).thenReturn(sampleResponseDTO);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Tâche de test")));
    }

    @Test
    @DisplayName("POST /api/v1/tasks - Échec de validation (400 Bad Request)")
    void createTask_withInvalidData_shouldReturn400BadRequest() throws Exception {
        TaskRequestDTO invalidDTO = TaskRequestDTO.builder()
                .title("") // Blank title
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")));
    }

    @Test
    @DisplayName("GET /api/v1/tasks - Retourne la liste des tâches (200 OK)")
    void getAllTasks_shouldReturnTaskList() throws Exception {
        when(taskService.getAllTasks(null, null, null)).thenReturn(List.of(sampleResponseDTO));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Tâche de test")));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Succès (200 OK)")
    void getTaskById_whenExists_shouldReturnTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(sampleResponseDTO);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Tâche de test")));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - Introuvable (404 Not Found)")
    void getTaskById_whenNotExists_shouldReturn404NotFound() throws Exception {
        when(taskService.getTaskById(99L)).thenThrow(new ResourceNotFoundException("Tâche", "id", 99L));

        mockMvc.perform(get("/api/v1/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - Succès de mise à jour (200 OK)")
    void updateTask_shouldReturn200Ok() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskRequestDTO.class))).thenReturn(sampleResponseDTO);

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/{id}/status - Succès (200 OK)")
    void updateTaskStatus_shouldReturn200Ok() throws Exception {
        when(taskService.updateTaskStatus(1L, Status.IN_PROGRESS)).thenReturn(sampleResponseDTO);

        mockMvc.perform(patch("/api/v1/tasks/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/{id}/priority - Succès (200 OK)")
    void updateTaskPriority_shouldReturn200Ok() throws Exception {
        when(taskService.updateTaskPriority(1L, Priority.HIGH)).thenReturn(sampleResponseDTO);

        mockMvc.perform(patch("/api/v1/tasks/1/priority")
                        .param("priority", "HIGH"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Succès de suppression (204 No Content)")
    void deleteTask_whenExists_shouldReturn204NoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - Introuvable (404 Not Found)")
    void deleteTask_whenNotExists_shouldReturn404NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Tâche", "id", 99L)).when(taskService).deleteTask(99L);

        mockMvc.perform(delete("/api/v1/tasks/99"))
                .andExpect(status().isNotFound());
    }
}
