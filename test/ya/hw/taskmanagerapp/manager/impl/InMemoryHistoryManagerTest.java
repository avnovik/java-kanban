package ya.hw.taskmanagerapp.manager.impl;

import ya.hw.taskmanagerapp.manager.HistoryManager;
import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task, task2;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task(1, "Task", "Desc", TaskStatus.NEW);
        task2 = new Task(2, "Task 2", "Description", TaskStatus.NEW);
    }

    @Test
    @DisplayName("Добавление задачи должно сохранять её в истории просмотров")
    void add_addsTaskToHistory() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertFalse(historyManager.getHistory().isEmpty(), "Проверяем добавление в историю");
        assertEquals(task, history.getFirst(), "В истории должна быть добавленная задача");
    }

    @Test
    @DisplayName("История просмотров должна быть пустой при инициализации")
    void getHistory_returnsEmptyListInitially() {
        assertTrue(historyManager.getHistory().isEmpty(), "Проверяем пустую историю при инициализации");
    }

    @Test
    @DisplayName("Повторное добавление одной задачи не должно создавать дубликатов в истории")
    void add_keepsDuplicateTasks() {
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size(),
                "При дублировании вызовов история НЕ должна сохранять одинаковые записи");
    }

    @Test
    @DisplayName("История просмотров должна сохранять порядок добавления задач (FIFO)")
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
    @DisplayName("Добавление null-задачи не должно влиять на историю просмотров")
    void add_nullTask_shouldNotFail() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty(),
                "Добавление null не должно влиять на историю");
    }

    @Test
    void remove_TaskFromHistory() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.remove(task.getId());
        assertEquals(List.of(task2), historyManager.getHistory());
    }
}
