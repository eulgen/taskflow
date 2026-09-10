package com.example.taskflowapi.e2e;

import com.example.taskflowapi.dto.TaskRequestDTO;
import com.example.taskflowapi.model.Priority;
import com.example.taskflowapi.model.Status;
import com.example.taskflowapi.repository.TaskRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskFlowE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        // Initialiser le port pour RestAssured
        RestAssured.port = port;
        
        // Nettoyer la base de données avant chaque test
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E - Flux complet: Création, récupération, modification de statut, puis suppression avec RestAssured")
    void fullTaskLifecycle_E2ETest() {
        
        TaskRequestDTO requestDTO = TaskRequestDTO.builder()
                .title("Tâche E2E")
                .description("Tester le cycle de vie complet")
                .priority(Priority.HIGH)
                .status(Status.TODO)
                .build();

        // 1. Création (POST)
        Integer taskId = given()
                .contentType(ContentType.JSON)
                .body(requestDTO)
            .when()
                .post("/api/v1/tasks")
            .then()
                .statusCode(201) // 201 CREATED
                .body("id", notNullValue())
                .body("title", equalTo("Tâche E2E"))
                .extract().path("id");

        // 2. Vérification de la création (GET par ID)
        given()
            .pathParam("id", taskId)
        .when()
            .get("/api/v1/tasks/{id}")
        .then()
            .statusCode(200) // 200 OK
            .body("id", equalTo(taskId))
            .body("priority", equalTo("HIGH"));

        // 3. Modification du statut (PATCH)
        given()
            .pathParam("id", taskId)
            .queryParam("status", "IN_PROGRESS")
        .when()
            .patch("/api/v1/tasks/{id}/status")
        .then()
            .statusCode(200) // 200 OK
            .body("status", equalTo("IN_PROGRESS"));

        // 4. Liste globale des tâches (GET)
        given()
        .when()
            .get("/api/v1/tasks")
        .then()
            .statusCode(200) // 200 OK
            .body("size()", equalTo(1))
            .body("[0].id", equalTo(taskId));

        // 5. Suppression (DELETE)
        given()
            .pathParam("id", taskId)
        .when()
            .delete("/api/v1/tasks/{id}")
        .then()
            .statusCode(204); // 204 NO CONTENT

        // 6. Vérification de la suppression (GET doit retourner 404)
        given()
            .pathParam("id", taskId)
        .when()
            .get("/api/v1/tasks/{id}")
        .then()
            .statusCode(404); // 404 NOT FOUND
    }
}
