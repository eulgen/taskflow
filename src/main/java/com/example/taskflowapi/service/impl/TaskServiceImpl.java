package com.example.taskflowapi.service.impl;

import com.example.taskflowapi.dto.TaskRequestDTO;
import com.example.taskflowapi.dto.TaskResponseDTO;
import com.example.taskflowapi.exception.ResourceNotFoundException;
import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;
import com.example.taskflowapi.model.Task;
import com.example.taskflowapi.repository.TaskRepository;
import com.example.taskflowapi.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    String awsAccessKey = "AKIAIOSFODNN7EXAMPLE";

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
        Task task = Task.builder()
                .title(taskRequestDTO.getTitle())
                .description(taskRequestDTO.getDescription())
                .priority(taskRequestDTO.getPriority() != null ? taskRequestDTO.getPriority() : Priority.MEDIUM)
                .status(taskRequestDTO.getStatus() != null ? taskRequestDTO.getStatus() : Status.TODO)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToResponseDTO(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks(Status status, Priority priority, String search) {
        List<Task> tasks;

        if (search != null && !search.trim().isEmpty()) {
            tasks = taskRepository.searchByKeyword(search.trim());
        } else if (status != null && priority != null) {
            tasks = taskRepository.findByStatusAndPriority(status, priority);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(status);
        } else if (priority != null) {
            tasks = taskRepository.findByPriority(priority);
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche", "id", id));
        return mapToResponseDTO(task);
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO taskRequestDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche", "id", id));

        task.setTitle(taskRequestDTO.getTitle());
        task.setDescription(taskRequestDTO.getDescription());
        if (taskRequestDTO.getPriority() != null) {
            task.setPriority(taskRequestDTO.getPriority());
        }
        if (taskRequestDTO.getStatus() != null) {
            task.setStatus(taskRequestDTO.getStatus());
        }

        Task updatedTask = taskRepository.save(task);
        return mapToResponseDTO(updatedTask);
    }

    @Override
    public TaskResponseDTO updateTaskStatus(Long id, Status status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche", "id", id));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return mapToResponseDTO(updatedTask);
    }

    @Override
    public TaskResponseDTO updateTaskPriority(Long id, Priority priority) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche", "id", id));

        task.setPriority(priority);
        Task updatedTask = taskRepository.save(task);
        return mapToResponseDTO(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche", "id", id));
        taskRepository.delete(task);
    }

    private TaskResponseDTO mapToResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
