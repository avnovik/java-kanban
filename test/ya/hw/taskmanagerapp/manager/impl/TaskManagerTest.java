package ya.hw.taskmanagerapp.manager.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.manager.exception.ManagerValidateException;
import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.Subtask;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected int taskId;
    protected int epicId;
    protected int subtaskId;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        taskManager = createManager();
        taskId = taskManager.createTask(new Task(0, "BeforeEach_Task", "Desc", TaskStatus.NEW, null, null));
        epicId = taskManager.createEpic(new Epic(0, "BeforeEach_Epic", "Desc"));
        subtaskId = taskManager.createSubtask(new Subtask(0, "BeforeEach_Subtask", "Desc", TaskStatus.NEW, epicId, null, null));
    }

    @Test
    @DisplayName("Создание и получение задачи")
    void shouldCreateAndGetTask() {
        assertNotNull(taskManager.getTask(taskId), "Задача не найдена");
    }

    @Test
    @DisplayName("Получение несуществующей задачи")
    void getTask_returnsNullForNonExistentId() {
        assertNull(taskManager.getTask(100500), "Найдена несуществующая задача");
    }

    @Test
    @DisplayName("Обновление задачи")
    void shouldUpdateTask() {
        Task updatedTask = new Task(taskId, "Updated", "New Desc", TaskStatus.DONE, null, null);
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTask(taskId);
        assertEquals("Updated", savedTask.getTitle());
        assertEquals("New Desc", savedTask.getDescription());
        assertEquals(TaskStatus.DONE, savedTask.getStatus());
    }

    @Test
    @DisplayName("Удаление задачи")
    void shouldDeleteTask() {
        taskManager.deleteTask(taskId);
        assertNull(taskManager.getTask(taskId), "Задача не удалилась");
    }

    @Test
    @DisplayName("Получение истории просмотров")
    void shouldGetHistory() {
        taskManager.getTask(taskId);
        taskManager.getEpic(epicId);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "Неверный размер истории");
        assertEquals(taskId, history.getFirst().getId(), "Неверный порядок задач в истории");
    }

    @Test
    @DisplayName("Обновление статуса эпика")
    void shouldUpdateEpicStatus() {
        for (Subtask subtask : taskManager.getSubtasksByEpicId(epicId)) {
            subtask.setStatus(TaskStatus.DONE);
            taskManager.updateSubtask(subtask);
        }

        assertEquals(TaskStatus.DONE, taskManager.getEpic(epicId).getStatus(), "Статус эпика не обновился");
    }

    @Test
    @DisplayName("Удаление эпика с подзадачами")
    void shouldDeleteEpicWithSubtasks() {
        taskManager.deleteEpic(epicId);
        assertTrue(taskManager.getSubtasksByEpicId(epicId).isEmpty(), "Подзадачи не удалились");
    }

    @Test
    @DisplayName("Добавление подзадачи обновляет статус эпика")
    void addSubtask_updatesEpicStatus() {
        Epic epic = taskManager.getEpic(epicId);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Проверяем статус NEW");

        taskManager.createSubtask(new Subtask(0, "Subtask", "Desc", TaskStatus.IN_PROGRESS, epicId, null, null));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Проверяем смену статуса на IN_PROGRESS");

        taskManager.createSubtask(new Subtask(0, "Subtask1", "Desc", TaskStatus.DONE, epicId, null, null));
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(), "Проверяем статус IN_PROGRESS");

        for (Subtask subtask : taskManager.getSubtasksByEpicId(epicId)) {
            subtask.setStatus(TaskStatus.DONE);
            taskManager.updateSubtask(subtask);
        }
        assertEquals(TaskStatus.DONE, taskManager.getEpic(epicId).getStatus(), "Проверяем смену статуса на DONE");
    }

    @Test
    @DisplayName("Сортировка задач по startTime (ранние -> поздние)")
    void getPrioritizedTasks_shouldReturnSortedTasks() {
        Task task1 = new Task(1, "Task1", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 12, 0), Duration.ofMinutes(30));
        Task task2 = new Task(2, "Task2", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(60));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        var prioritized = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals(task2, ((TreeSet<Task>) prioritized).first());
    }

    @Test
    @DisplayName("Удаление эпика удаляет его подзадачи")
    void deleteEpic_removesSubtasks() {
        taskManager.deleteEpic(epicId);
        assertNull(taskManager.getEpic(epicId));
        assertTrue(taskManager.getSubtasksByEpicId(epicId).isEmpty());
    }

    @Test
    @DisplayName("Задачи без 'startTime' не сортируются")
    void getPrioritizedTasks_ignoresTasksWithoutTime() {
        Task task1 = new Task(1, "Task1", "Desc", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task(2, "Task2", "Desc", TaskStatus.NEW, null, null);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    @DisplayName("Вызов исключения при пересечении времени выполнения задач")
    void shouldThrowExceptionForTimeOverlap() {
        Task task1 = new Task(1, "Task1", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(120));

        taskManager.createTask(task1);

        Task task2 = new Task(2, "Task2", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 11, 0),
                Duration.ofMinutes(30));

        assertThrows(ManagerValidateException.class, () -> taskManager.createTask(task2));
    }

    @Test
    @DisplayName("Обновление времени эпика при изменении подзадач")
    void updateEpicTime_afterSubtaskChange() {
        Epic epic = new Epic(1, "Epic", "Desc");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Sub", "Desc", TaskStatus.NEW, epicId,
                LocalDateTime.of(2023, 1, 1, 9, 0),
                Duration.ofMinutes(60));

        taskManager.createSubtask(subtask);
        assertEquals(LocalDateTime.of(2023, 1, 1, 9, 0),
                taskManager.getEpic(epicId).getStartTime(), "Время эпика должно обновиться после добавления подзадачи");
    }

    @Test
    @DisplayName("Проверка приоритизации задач")
    void shouldPrioritizeTasks() {
        Task task1 = new Task(1, "Task1", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(1));
        taskManager.createTask(task1);

        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Задача не добавилась в приоритизированный список");
    }

    @Test
    @DisplayName("Проверка пересечения времени задач")
    void shouldDetectTimeOverlap() {
        Task task1 = new Task(1, "Task1", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofHours(2));

        Task task2 = new Task(2, "Task2", "Desc", TaskStatus.NEW,
                LocalDateTime.of(2023, 1, 1, 11, 0), Duration.ofHours(1));

        taskManager.createTask(task1);
        assertThrows(ManagerValidateException.class, () -> taskManager.createTask(task2),
                "Не обнаружено пересечение времени");
    }

    @Test
    @DisplayName("При удалении эпика подзадачи не удаляются из сортированного списка")
    void deleteEpic_removesSubtasksFromPrioritized() {
        Epic epic = new Epic(1, "Epic", "Desc");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Sub", "Desc", TaskStatus.NEW, epicId,
                LocalDateTime.now(), Duration.ofMinutes(30));

        taskManager.createSubtask(subtask);
        assertEquals(1, taskManager.getPrioritizedTasks().size(),
                "Подзадача должна добавиться в prioritizedTasks");

        taskManager.deleteEpic(epicId);
        assertEquals(1, taskManager.getPrioritizedTasks().size(),
                "После удаления эпика подзадачи остаются в prioritizedTasks (текущая реализация)");
    }

    @Test
    @DisplayName("При удалении всех подзадач сбрасывается время и статус эпика")
    void deleteAllSubtasks_resetsEpicTimeAndStatus() {
        Epic epic = new Epic(1, "Epic", "Desc");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Sub", "Desc", TaskStatus.IN_PROGRESS, epicId,
                LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);

        Epic savedEpic = taskManager.getEpic(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(),
                "Статус эпика должен соответствовать подзадачам");
        assertNotNull(savedEpic.getStartTime(),
                "Время начала эпика должно быть установлено");
        assertNotNull(savedEpic.getEndTime(),
                "Время окончания эпика должно быть установлено");
        assertNotEquals(Duration.ZERO, savedEpic.getDuration(),
                "Длительность эпика должна быть больше 0");

        taskManager.deleteAllSubtasks();

        Epic updatedEpic = taskManager.getEpic(epicId);
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(),
                "После удаления подзадач статус эпика должен сброситься на NEW");
        assertNull(updatedEpic.getStartTime(),
                "Время начала эпика должно сброситься в null");
        assertNull(updatedEpic.getEndTime(),
                "Время окончания эпика должно сброситься в null");
        assertEquals(Duration.ZERO, updatedEpic.getDuration(),
                "Длительность эпика должна сброситься на 0");
    }
}