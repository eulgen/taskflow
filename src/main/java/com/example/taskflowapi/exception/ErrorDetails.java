package com.example.taskflowapi.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Structure standard des réponses d'erreur")
public class ErrorDetails {

    @Schema(description = "Horodatage de l'erreur", example = "2026-09-04T10:45:00")
    private LocalDateTime timestamp;

    @Schema(description = "Code de statut HTTP", example = "404")
    private int status;

    @Schema(description = "Type de l'erreur HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Message explicatif de l'erreur", example = "Tâche non trouvée avec id : '99'")
    private String message;

    @Schema(description = "Chemin URI de la requête", example = "/api/v1/tasks/99")
    private String path;

    @Schema(description = "Détail des erreurs de validation par champ (facultatif)")
    private Map<String, String> validationErrors;
}
