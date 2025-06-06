package ya.hw.taskmanagerapp.manager.impl;

import org.junit.jupiter.api.DisplayName;
import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private TaskManager manager;
    int taskId;
    int subtaskId;
    int epicId;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        taskId = manager.createTask(new Task(0, "Task", "Desc", TaskStatus.NEW, null, null));
        epicId = manager.createEpic(new Epic(0, "Epic", "Desc"));
        subtaskId = manager.createSubtask(new Subtask(0, "Subtask", "Desc", TaskStatus.NEW, epicId, null, null));
    }

    @Test
    void createTask_addsNewTask() {
        assertNotNull(manager.getTask(taskId), "Не найдена созданная задача");
    }

    @Test
    void getTask_returnsNullForNonExistentId() {
        int nonExistentId = 100500;
        assertNull(manager.getTask(nonExistentId), "Найдена не существующая задача");
    }

    @Test
    void updateTask_updatesExistingTask() {
        Task updatedTask = new Task(taskId, "Updated Task", "New Desc", TaskStatus.IN_PROGRESS, null, null);
        manager.updateTask(updatedTask);

        Task savedTask = manager.getTask(taskId);
        assertEquals("Updated Task", savedTask.getTitle());
        assertEquals("New Desc", savedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, savedTask.getStatus());
    }

    @Test
    void getHistory_returnsViewHistory() {
        manager.getTask(taskId);
        manager.getEpic(epicId);
        manager.getSubtask(subtaskId);

        List<Task> history = manager.getHistory();
        assertEquals(3, history.size());
        assertEquals(taskId, history.get(0).getId());
        assertEquals(epicId, history.get(1).getId());
        assertEquals(subtaskId, history.get(2).getId());
    }

    @Test
    void addSubtask_updatesEpicStatus() {
        Epic epic = manager.getEpic(epicId);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Проверяем статус NEW");

        manager.createSubtask(new Subtask(0, "Subtask", "Desc", TaskStatus.IN_PROGRESS, epicId, null, null));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Проверяем смену статуса на IN_PROGRESS");

        manager.createSubtask(new Subtask(0, "Subtask1", "Desc", TaskStatus.DONE, epicId, null, null));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epicId).getStatus(), "Проверяем статус IN_PROGRESS");

        for (Subtask subtask : manager.getSubtasksByEpicId(epicId)) {
            subtask.setStatus(TaskStatus.DONE);
            manager.updateSubtask(subtask);
        }
        assertEquals(TaskStatus.DONE, manager.getEpic(epicId).getStatus(), "Проверяем смену статуса на DONE");
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        int taskId = manager.createTask(new Task(0, "Task", "Desc", TaskStatus.NEW, null, null));
        int epicId = manager.createEpic(new Epic(0, "Epic", "Desc"));
        int subtaskId = manager.createSubtask(new Subtask(0, "Subtask", "Desc", TaskStatus.NEW, epicId, null, null));

        assertNotNull(manager.getTask(taskId), "Не найдена обычная задача");
        assertNotNull(manager.getEpic(epicId), "Не найден эпик");
        assertNotNull(manager.getSubtask(subtaskId), "Не найдена подзадача");
    }

    @Test
    void deleteTask_removesTaskFromManager() {
        manager.deleteTask(taskId);
        assertNull(manager.getTask(taskId));
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void deleteEpic_removesSubtasks() {
        manager.deleteEpic(epicId);
        assertNull(manager.getEpic(epicId));
        assertTrue(manager.getSubtasksByEpicId(epicId).isEmpty());
    }

    @Test
    @DisplayName("Задачи без 'startTime' не сортируются")
    void getPrioritizedTasks_ignoresTasksWithoutTime() {
        Task task1 = new Task(1, "Task1", "Desc", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task(2, "Task2", "Desc", TaskStatus.NEW, null, null);

        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(1, manager.getPrioritizedTasks().size());
    }

    @Test
    @DisplayName("Обновление времени эпика при изменении подзадач")
    void updateEpicTime_afterSubtaskChange() {
        Epic epic = new Epic(1, "Epic", "Desc");
        int epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Sub", "Desc", TaskStatus.NEW, epicId,
                LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(60));

        manager.createSubtask(subtask);
        assertEquals(LocalDateTime.of(2023, 1, 1, 9, 0),
                manager.getEpic(epicId).getStartTime(), "Время эпика должно обновиться после добавления подзадачи");
    }
}
