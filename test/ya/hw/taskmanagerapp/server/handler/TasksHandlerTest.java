package ya.hw.taskmanagerapp.server.handler;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тесты для обработчика /tasks (TasksHandler)")
public class TasksHandlerTest extends HandlerBaseTest {

    @Test
    @DisplayName("POST /tasks - создание простой задачи без времени")
    void createSmokeTask_ShouldReturn201() throws Exception {
        String json = """
                {
                    "id": 0,
                    "title": "Test Task",
                    "description": "Test Description",
                    "status": "NEW"
                }
                """;

        HttpResponse<String> response = sendPost("/tasks", json);

        assertResponseCode(response, 201);
        assertJsonContains(response, "Задача создана");
        assertEquals(1, taskServer.getManager().getAllTasks().size(), "Задача должна добавиться в менеджер");
    }

    @Test
    @DisplayName("POST /tasks - создание задачи")
    void createTask_ShouldReturn201() throws Exception {
        Task task = new Task(0, "Test", "Description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendPost("/tasks", taskJson);

        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size(), "Задача не добавилась в менеджер");
        assertEquals("Test", tasks.getFirst().getTitle(), "Название задачи не совпадает");

        assertTrue(jsonResponse.has("id"), "Ответ должен содержать поле 'id'");
        int taskId = jsonResponse.get("id").getAsInt();
        assertTrue(taskId > 0, "ID задачи должен быть положительным");
    }

    @Test
    @DisplayName("POST /tasks - обновление задачи (существующий ID)")
    void updateTask_ShouldReturn200() throws Exception {
        Task task = new Task(0, "Old", "Desc", TaskStatus.NEW, null, null);
        int taskId = manager.createTask(task);

        Task updatedTask = new Task(taskId, "New", "Updated", TaskStatus.DONE, null, null);
        String json = gson.toJson(updatedTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("New", manager.getTask(taskId).getTitle());
    }

    @Test
    @DisplayName("POST /tasks - возвращает 406 при пересечении времени")
    void createTask_ShouldReturn406_WhenTimeOverlaps() throws Exception {
        LocalDateTime time = LocalDateTime.now();
        Task task1 = new Task(0, "Task1", "Desc", TaskStatus.NEW, time, Duration.ofHours(1));
        manager.createTask(task1);

        // Пересекающаяся задача
        Task task2 = new Task(0, "Task2", "Desc", TaskStatus.NEW, time.plusMinutes(30), Duration.ofHours(1));
        String json = gson.toJson(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
        assertEquals("{\"error\":\"Пересечение времени\"}", response.body());
    }

    @Test
    @DisplayName("GET /tasks - получение списка задач (пустой список)")
    void getAllTasks_ShouldReturnEmptyList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertEquals("[]", response.body(), "Тело ответа не пустой массив");
    }

    @Test
    @DisplayName("GET /tasks/{id} - успешное получение задачи")
    void getTaskById_ShouldReturn200() throws Exception {
        int taskId = createTestTask("Test", "Description");

        HttpResponse<String> response = sendGet("/tasks/" + taskId);

        assertResponseCode(response, 200);
        assertJsonContains(response, "Test");
        assertJsonContains(response, "Description");
    }

    @Test
    @DisplayName("GET /tasks/{id} - возвращает 404, если задачи нет")
    void getTaskById_ShouldReturn404() throws Exception {
        int nonExistentId = 999;

        HttpResponse<String> response = sendGet("/tasks/" + nonExistentId);

        assertResponseCode(response, 404);
        assertJsonContains(response, "{\"error\":\"Not Found\"}");
    }

    @Test
    @DisplayName("DELETE /tasks/{id} - удаление задачи")
    void deleteTask_ShouldReturn200() throws Exception {
        int taskId = createTestTask("To Delete", "Desc");

        HttpResponse<String> response = sendDelete("/tasks/" + taskId);

        assertResponseCode(response, 200);
        assertTrue(manager.getAllTasks().isEmpty(), "Задача не удалилась");
    }
}