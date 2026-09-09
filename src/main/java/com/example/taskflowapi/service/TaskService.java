package com.example.taskflowapi.service;

import com.example.taskflowapi.dto.TaskRequestDTO;
import com.example.taskflowapi.dto.TaskResponseDTO;
import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;

import java.util.List;

public interface TaskService {
    TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO);

    List<TaskResponseDTO> getAllTasks(Status status, Priority priority, String search);

    TaskResponseDTO getTaskById(Long id);

    TaskResponseDTO updateTask(Long id, TaskRequestDTO taskRequestDTO);

    TaskResponseDTO updateTaskStatus(Long id, Status status);

    TaskResponseDTO updateTaskPriority(Long id, Priority priority);

    void deleteTask(Long id);
}
