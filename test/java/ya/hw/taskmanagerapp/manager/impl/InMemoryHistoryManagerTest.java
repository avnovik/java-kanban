package ya.hw.taskmanagerapp.manager.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.manager.HistoryManager;
import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task(1, "Task", "Desc", TaskStatus.NEW);
    }
    @Test
    void add_addsTaskToHistory() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertFalse(historyManager.getHistory().isEmpty(), "Проверяем добавление в историю");
        assertEquals(task, history.get(0), "В истории должна быть добавленная задача");
    }

    @Test
    void getHistory_returnsEmptyListInitially() {
        assertTrue(historyManager.getHistory().isEmpty(), "Проверяем пустую историю при инициализации");
    }

    @Test
    void add_limitsHistoryTo10Tasks() {
        for (int i = 1; i <= 10; i++) {
            Task task = new Task(i, "Task " + i, "Desc", TaskStatus.NEW);
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size());

        Task extraTask = new Task(11, "Extra", "Desc", TaskStatus.NEW);
        historyManager.add(extraTask);
        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "Проверяем ограничение истории 10 задачами");
        assertFalse(history.contains(new Task(1, "Task 1", "Desc", TaskStatus.NEW)),
                "Первая задача должна быть удалена");
        assertTrue(history.contains(new Task(11, "Task 11", "Desc", TaskStatus.NEW)),
                "Новая задача должна быть в истории");
    }

    @Test
    void add_keepsDuplicateTasks() {
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(2, historyManager.getHistory().size(),
                "При дублировании вызовов история должна сохранять все записи");
    }

    @Test
    void getHistory_returnsTasksInFIFOOrder() {
        Task task1 = new Task(1, "Task1", "", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(task1, history.get(0), "Первая задача должна быть первой в истории");
        assertEquals(task2, history.get(1), "Вторая задача должна быть второй в истории");
    }

    @Test
    void add_nullTask_shouldNotFail() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty(),
                "Добавление null не должно влиять на историю");
    }
}
