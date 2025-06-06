package ya.hw.taskmanagerapp.manager.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.manager.exception.ManagerValidateException;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TaskPriorityTest {
    private final TaskManager manager = Managers.getDefault();

    @Test
    @DisplayName("Сортировка задач по startTime (ранние -> поздние)")
    void getPrioritizedTasks_shouldReturnSortedTasks() {
        Task task1 = new Task(1, "Task1", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 12, 0), Duration.ofMinutes(30));
        Task task2 = new Task(2, "Task2", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(60));

        manager.createTask(task1);
        manager.createTask(task2);

        var prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals(task2, ((TreeSet<Task>) prioritized).first());
    }

    @Test
    @DisplayName("Вызов исключения при пересечении времени выполнения задач")
    void shouldThrowExceptionForTimeOverlap() {
        Task task1 = new Task(1, "Task1", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(120));

        manager.createTask(task1);

        Task task2 = new Task(2, "Task2", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 11, 0),
                Duration.ofMinutes(30));

        assertThrows(ManagerValidateException.class, () -> manager.createTask(task2));
    }
}