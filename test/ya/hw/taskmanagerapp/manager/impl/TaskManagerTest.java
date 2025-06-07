package ya.hw.taskmanagerapp.manager.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.Subtask;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.util.List;

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
        taskId = taskManager.createTask(new Task(0, "Task", "Desc", TaskStatus.NEW, null, null));
        epicId = taskManager.createEpic(new Epic(0, "Epic", "Desc"));
        subtaskId = taskManager.createSubtask(new Subtask(0, "Subtask", "Desc", TaskStatus.NEW, epicId, null, null));
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
}
