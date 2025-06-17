package ya.hw.taskmanagerapp.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.server.HttpTaskServer;
import ya.hw.taskmanagerapp.server.adapter.DurationAdapter;
import ya.hw.taskmanagerapp.server.adapter.LocalDateTimeAdapter;
import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.Subtask;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HandlerBaseTest {
    protected static final int PORT = 8080;
    protected static final String BASE_URL = "http://localhost:" + PORT;

    protected HttpTaskServer taskServer;
    protected TaskManager manager;
    protected final HttpClient httpClient = HttpClient.newHttpClient();
    protected Gson gson;

    @BeforeEach
    void setUpBase() throws IOException {
        taskServer = new HttpTaskServer();
        manager = taskServer.getManager();
        taskServer.start();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @AfterEach
    void tearDownBase() {
        taskServer.stop();
    }

    protected int createTestTask(String title, String description) {
        Task task = new Task(0, title, description, TaskStatus.NEW, null, null);
        return manager.createTask(task);
    }

    protected int createTestEpic(String title, String description) {
        Epic epic = new Epic(0, title, description);
        return manager.createEpic(epic);
    }

    protected int createTestSubtask(String title, String description, int epicId) {
        Subtask subtask = new Subtask(0, title, description, TaskStatus.NEW, epicId, null, null);
        return manager.createSubtask(subtask);
    }

    protected HttpResponse<String> sendGet(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> sendPost(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> sendDelete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected void assertResponseCode(HttpResponse<String> response, int expectedCode) {
        assertEquals(expectedCode, response.statusCode(),
                "Ожидался код ответа " + expectedCode + ", но получен " + response.statusCode());
    }

    protected void assertJsonContains(HttpResponse<String> response, String expectedText) {
        assertTrue(response.body().contains(expectedText),
                "Ответ не содержит ожидаемый текст: " + expectedText);
    }

    protected void assertJsonDoesNotContain(HttpResponse<String> response, String unexpectedText) {
        assertFalse(response.body().contains(unexpectedText),
                "Ответ содержит неожидаемый текст: " + unexpectedText);
    }
}