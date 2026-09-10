package com.example.taskflowapi.controller;

import com.example.taskflowapi.dto.TaskRequestDTO;
import com.example.taskflowapi.dto.TaskResponseDTO;
import com.example.taskflowapi.exception.ErrorDetails;
import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;
import com.example.taskflowapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Gestion des Tâches", description = "Endpoints pour créer, lire, modifier et supprimer des tâches")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle tâche", description = "Ajoute une nouvelle tâche au système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tâche créée avec succès",
                    content = @Content(schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données de requête invalides",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO taskRequestDTO) {
        TaskResponseDTO createdTask = taskService.createTask(taskRequestDTO);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);        
    }

    @GetMapping
    @Operation(summary = "Récupérer la liste des tâches", description = "Permet d'obtenir toutes les tâches avec des filtres optionnels sur le statut, la priorité ou par mot-clé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des tâches récupérée avec succès")
    })
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(
            @Parameter(description = "Filtrer par statut (TODO, IN_PROGRESS, DONE)") @RequestParam(required = false) Status status,
            @Parameter(description = "Filtrer par priorité (LOW, MEDIUM, HIGH)") @RequestParam(required = false) Priority priority,
            @Parameter(description = "Rechercher dans le titre ou la description") @RequestParam(required = false) String search) {

        List<TaskResponseDTO> tasks = taskService.getAllTasks(status, priority, search);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une tâche par son identifiant", description = "Retourne les détails complets d'une tâche spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tâche trouvée",
                    content = @Content(schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @Parameter(description = "Identifiant de la tâche", required = true) @PathVariable Long id) {

        TaskResponseDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour complètement une tâche", description = "Remplace les informations d'une tâche existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tâche mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = TaskResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    public ResponseEntity<TaskResponseDTO> updateTask(
            @Parameter(description = "Identifiant de la tâche à modifier", required = true) @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO taskRequestDTO) {

        TaskResponseDTO updatedTask = taskService.updateTask(id, taskRequestDTO);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une tâche", description = "Modifie uniquement le statut d'une tâche")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @Parameter(description = "Identifiant de la tâche", required = true) @PathVariable Long id,
            @Parameter(description = "Nouveau statut", required = true) @RequestParam Status status) {

        TaskResponseDTO updatedTask = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/priority")
    @Operation(summary = "Mettre à jour la priorité d'une tâche", description = "Modifie uniquement la priorité d'une tâche")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Priorité mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée")
    })
    public ResponseEntity<TaskResponseDTO> updateTaskPriority(
            @Parameter(description = "Identifiant de la tâche", required = true) @PathVariable Long id,
            @Parameter(description = "Nouvelle priorité", required = true) @RequestParam Priority priority) {

        TaskResponseDTO updatedTask = taskService.updateTaskPriority(id, priority);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une tâche", description = "Supprime définitivement une tâche du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tâche supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Tâche non trouvée",
                    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Identifiant de la tâche à supprimer", required = true) @PathVariable Long id) {

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
