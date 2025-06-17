package ya.hw.taskmanagerapp.server.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.Subtask;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для обработчика /epics (EpicsHandler)")
public class EpicsHandlerTest extends HandlerBaseTest {

    @Test
    @DisplayName("GET /epics - получение пустого списка эпиков")
    void getAllEpics_ShouldReturnEmptyList() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    @DisplayName("GET /epics/{id} - успешное получение эпика")
    void getEpicById_ShouldReturn200() throws Exception {
        Epic epic = new Epic(0, "Test Epic", "Description");
        int epicId = manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test Epic"));
        assertTrue(response.body().contains("Description"));
    }

    @Test
    @DisplayName("GET /epics/{id} - возвращает 404, если эпика нет")
    void getEpic_ShouldReturn404_WhenEpicNotExists() throws Exception {
        assertNull(manager.getEpic(999), "Эпик не должен существовать");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("{\"error\":\"Not Found\"}", response.body());
    }

    @Test
    @DisplayName("GET /epics/{id}/subtasks - получение подзадач эпика")
    void getEpicSubtasks_ShouldReturn200() throws Exception {
        Epic epic = new Epic(0, "Epic", "Desc");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Subtask", "Desc", TaskStatus.NEW, epicId, null, null);
        manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask"));
    }

    @Test
    @DisplayName("GET /epics/{id}/subtasks - возвращает 404, если эпика нет")
    void getEpicSubtasks_ShouldReturn404_WhenEpicNotExists() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("{\"error\":\"Not Found\"}", response.body());
    }

    @Test
    @DisplayName("POST /epics - создание эпика")
    void createEpic_ShouldReturn201() throws Exception {
        String json = """
                {
                    "title": "Test Epic",
                    "description": "Test Description"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    @DisplayName("POST /epics - 400 при пустом теле")
    void postEpic_EmptyBody_Returns400() throws Exception {
        HttpResponse<String> response = sendPost("/epics", "");
        assertResponseCode(response, 400);
        assertEquals("{\"error\":\"Request body is empty\"}", response.body());
    }

    @Test
    @DisplayName("DELETE /epics/{id} - удаление эпика")
    void deleteEpic_ShouldReturn200() throws Exception {
        Epic epic = new Epic(0, "Test Epic", "Desc");
        int epicId = manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());
    }
}