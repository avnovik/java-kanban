package ya.hw.taskmanagerapp.server.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для обработчика /history (HistoryHandler)")
public class HistoryHandlerTest extends HandlerBaseTest {

    @Test
    @DisplayName("GET /history - пустая история")
    void getHistory_ShouldReturnEmptyList() throws Exception {
        HttpResponse<String> response = sendGet("/history");
        assertResponseCode(response, 200);
        assertEquals("[]", response.body());
    }

    @Test
    @DisplayName("GET /history - история с задачами")
    void getHistory_ShouldReturnTasks() throws Exception {
        int taskId = createTestTask("Task", "Desc");
        manager.getTask(taskId);

        HttpResponse<String> response = sendGet("/history");
        assertResponseCode(response, 200);
        assertTrue(response.body().contains("Task"));
    }

    @Test
    @DisplayName("GET /history - удалённые задачи исчезают из истории")
    void getHistory_ShouldNotContainDeletedTasks() throws Exception {
        int taskId = createTestTask("Task", "Desc");
        manager.getTask(taskId);

        manager.deleteTask(taskId);

        HttpResponse<String> response = sendGet("/history");
        assertResponseCode(response, 200);
        assertFalse(response.body().contains("Task"), "Удалённая задача осталась в истории");
    }
}