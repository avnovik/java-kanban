package ya.hw.taskmanagerapp.server.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.task.Subtask;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для обработчика /subtasks (SubtasksHandler)")
public class SubtasksHandlerTest extends HandlerBaseTest {

    @Test
    @DisplayName("GET /subtasks - пустой список подзадач")
    void getAllSubtasks_ShouldReturnEmptyList() throws Exception {
        HttpResponse<String> response = sendGet("/subtasks");
        assertResponseCode(response, 200);
        assertEquals("[]", response.body());
    }

    @Test
    @DisplayName("GET /subtasks/{id} - успешное получение подзадачи")
    void getSubtaskById_ShouldReturn200() throws Exception {
        int epicId = createTestEpic("Epic", "Desc");
        int subtaskId = createTestSubtask("Subtask", "Desc", epicId);

        HttpResponse<String> response = sendGet("/subtasks/" + subtaskId);
        assertResponseCode(response, 200);
        assertJsonContains(response, "Subtask");
    }

    @Test
    @DisplayName("GET /subtasks/{id} - 404 если подзадачи нет")
    void getSubtaskById_ShouldReturn404() throws Exception {
        HttpResponse<String> response = sendGet("/subtasks/999");
        assertResponseCode(response, 404);
        assertEquals("{\"error\":\"Not Found\"}", response.body());
    }

    @Test
    @DisplayName("POST /subtasks - создание подзадачи")
    void createSubtask_ShouldReturn201() throws Exception {
        int epicId = createTestEpic("Epic", "Desc");
        String json = """
                {
                    "title": "New Subtask",
                    "description": "Desc",
                    "status": "NEW",
                    "epicId": %d
                }
                """.formatted(epicId);

        HttpResponse<String> response = sendPost("/subtasks", json);
        assertResponseCode(response, 201);
        assertTrue(response.body().contains("\"id\""));
    }

    @Test
    @DisplayName("POST /subtasks - обновление подзадачи")
    void updateSubtask_ShouldReturn200() throws Exception {
        int epicId = createTestEpic("Epic", "Desc");
        int subtaskId = createTestSubtask("Old", "Desc", epicId);

        String json = """
                {
                    "id": %d,
                    "title": "Updated",
                    "description": "New Desc",
                    "status": "DONE",
                    "epicId": %d
                }
                """.formatted(subtaskId, epicId);

        HttpResponse<String> response = sendPost("/subtasks", json);
        assertResponseCode(response, 200);
        assertEquals(TaskStatus.DONE, manager.getSubtask(subtaskId).getStatus());
    }

    @Test
    @DisplayName("POST /subtasks - 406 при пересечении времени")
    void createSubtask_ShouldReturn406_WhenTimeOverlaps() throws Exception {
        int epicId = createTestEpic("Epic", "Desc");
        LocalDateTime time = LocalDateTime.now();
        Subtask subtask1 = new Subtask(0, "Subtask1", "Desc", TaskStatus.NEW, epicId,
                time, Duration.ofMinutes(30));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask(12, "Subtask2", "Desc", TaskStatus.NEW, epicId,
                time.plusMinutes(15), Duration.ofMinutes(60)
        );

        HttpResponse<String> response = sendPost("/subtasks", gson.toJson(subtask2));

        assertResponseCode(response, 406);
        assertEquals("{\"error\":\"Пересечение времени\"}", response.body());
    }

    @Test
    @DisplayName("POST /subtasks - 400 при пустом теле")
    void postEpic_EmptyBody_Returns400() throws Exception {
        HttpResponse<String> response = sendPost("/subtasks", "");
        assertResponseCode(response, 400);
        assertEquals("{\"error\":\"Request body is empty\"}", response.body());
    }

    @Test
    @DisplayName("DELETE /subtasks/{id} - удаление подзадачи")
    void deleteSubtask_ShouldReturn200() throws Exception {
        int epicId = createTestEpic("Epic", "Desc");
        int subtaskId = createTestSubtask("To Delete", "Desc", epicId);

        HttpResponse<String> response = sendDelete("/subtasks/" + subtaskId);
        assertResponseCode(response, 200);
        assertNull(manager.getSubtask(subtaskId));
    }
}
