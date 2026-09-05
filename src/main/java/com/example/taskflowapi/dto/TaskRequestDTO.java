package com.example.taskflowapi.dto;

import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO pour la création ou mise à jour d'une tâche")
public class TaskRequestDTO {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 2, max = 100, message = "Le titre doit contenir entre 2 et 100 caractères")
    @Schema(description = "Titre de la tâche", example = "Implémenter l'authentification", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Schema(description = "Description détaillée de la tâche", example = "Mettre en place JWT et Spring Security")
    private String description;

    @Schema(description = "Priorité de la tâche (LOW, MEDIUM, HIGH)", example = "HIGH")
    private Priority priority;

    @Schema(description = "Statut de la tâche (TODO, IN_PROGRESS, DONE)", example = "TODO")
    private Status status;
}
