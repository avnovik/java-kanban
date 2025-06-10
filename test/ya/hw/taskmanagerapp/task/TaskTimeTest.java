package ya.hw.taskmanagerapp.task;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TaskTimeTest {

    @Test
    @DisplayName("Расчет endTime для задачи с указанным временем")
    void task_shouldCalculateEndTimeCorrectly() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        Duration duration = Duration.ofMinutes(90);
        Task task = new Task(1, "Task", "Desc", TaskStatus.NEW, startTime, duration);

        assertEquals(startTime.plus(duration), task.getEndTime());
    }

    @Test
    @DisplayName("Эпик корректно рассчитывает время на основе подзадач")
    void epic_shouldCalculateTimeFromSubtasks() {
        TaskManager manager = Managers.getDefault();

        var starSubtask1 = LocalDateTime.of(2025, 1, 1, 10, 0);
        var starSubtask2 = LocalDateTime.of(2025, 1, 1, 11, 0);

        Epic epic = new Epic(1, "Epic", "Desc");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask(2, "Sub1", "Desc", TaskStatus.NEW, 1,
                starSubtask1, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask(3, "Sub2", "Desc", TaskStatus.DONE, 1,
                starSubtask2, Duration.ofMinutes(60));

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(starSubtask1, epic.getStartTime());
        assertEquals(Duration.ofMinutes(90), epic.getDuration());
        assertEquals(starSubtask2.plusMinutes(60), epic.getEndTime());
    }

    @Test
    @DisplayName("Задача без startTime возвращает null для endTime")
    void taskWithoutTime_shouldReturnNullEndTime() {
        Task task = new Task(1, "Task", "Desc", TaskStatus.NEW, null, null);
        assertNull(task.getEndTime());
    }
}