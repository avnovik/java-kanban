package ya.hw.taskmanagerapp.manager.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.Subtask;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

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
        taskId = manager.createTask(new Task(0, "Task", "Desc", TaskStatus.NEW));
        epicId = manager.createEpic(new Epic(0, "Epic", "Desc"));
        subtaskId = manager.createSubtask(new Subtask(0, "Subtask", "Desc", TaskStatus.NEW, epicId));
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
        Task updatedTask = new Task(taskId, "Updated Task", "New Desc", TaskStatus.IN_PROGRESS);
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
        assertEquals(TaskStatus.NEW,epic.getStatus(), "Проверяем статус NEW");

        manager.createSubtask(new Subtask(0, "Subtask", "Desc", TaskStatus.IN_PROGRESS, epicId));
        assertEquals(TaskStatus.IN_PROGRESS,epic.getStatus(), "Проверяем смену статуса на IN_PROGRESS");

        manager.createSubtask(new Subtask(0, "Subtask1", "Desc", TaskStatus.DONE, epicId));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epicId).getStatus(), "Проверяем статус IN_PROGRESS");

        for (Subtask subtask : manager.getSubtasksByEpicId(epicId)) {
            subtask.setStatus(TaskStatus.DONE);
            manager.updateSubtask(subtask);
        }
        assertEquals(TaskStatus.DONE, manager.getEpic(epicId).getStatus(), "Проверяем смену статуса на DONE");
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        int taskId = manager.createTask(new Task(0, "Task", "Desc", TaskStatus.NEW));
        int epicId = manager.createEpic(new Epic(0, "Epic", "Desc"));
        int subtaskId = manager.createSubtask(new Subtask(0, "Subtask", "Desc", TaskStatus.NEW, epicId));

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
}
