package ya.hw.taskmanagerapp.server.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для обработчика /prioritized (PrioritizedHandler)")
public class PrioritizedHandlerTest extends HandlerBaseTest {

    @Test
    @DisplayName("GET /prioritized - пустой список")
    void getPrioritized_ShouldReturnEmptyList() throws Exception {
        HttpResponse<String> response = sendGet("/prioritized");
        assertResponseCode(response, 200);
        assertEquals("[]", response.body());
    }

    @Test
    @DisplayName("GET /prioritized - задачи в порядке приоритета")
    void getPrioritized_ShouldReturnSortedTasks() throws Exception {
        Task task1 = new Task(0, "Task 1", "Desc", TaskStatus.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        Task task2 = new Task(0, "Task 2", "Desc", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(15));

        manager.createTask(task2);
        manager.createTask(task1);

        HttpResponse<String> response = sendGet("/prioritized");
        assertResponseCode(response, 200);

        // Проверяем порядок: task2 должен быть раньше task1
        int indexTask2 = response.body().indexOf("Task 2");
        int indexTask1 = response.body().indexOf("Task 1");
        assertTrue(indexTask2 < indexTask1, "Задачи не отсортированы по startTime");
    }
}
