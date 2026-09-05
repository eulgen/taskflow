package com.example.taskflowapi.dto;

import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO représentant les détails d'une tâche retournée par l'API")
public class TaskResponseDTO {

    @Schema(description = "Identifiant unique de la tâche", example = "1")
    private Long id;

    @Schema(description = "Titre de la tâche", example = "Implémenter l'authentification")
    private String title;

    @Schema(description = "Description de la tâche", example = "Mettre en place JWT et Spring Security")
    private String description;

    @Schema(description = "Priorité de la tâche", example = "HIGH")
    private Priority priority;

    @Schema(description = "Statut actuel de la tâche", example = "IN_PROGRESS")
    private Status status;

    @Schema(description = "Date de création de la tâche")
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière modification")
    private LocalDateTime updatedAt;
}
