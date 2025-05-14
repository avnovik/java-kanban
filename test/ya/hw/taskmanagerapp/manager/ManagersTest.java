package ya.hw.taskmanagerapp.manager;

import main.ya.hw.taskmanagerapp.manager.HistoryManager;
import main.ya.hw.taskmanagerapp.manager.Managers;
import main.ya.hw.taskmanagerapp.manager.TaskManager;
import main.ya.hw.taskmanagerapp.manager.impl.InMemoryHistoryManager;
import main.ya.hw.taskmanagerapp.manager.impl.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManagersTest {
    @Test
    void shouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Менеджер задач не инициализирован");
        assertTrue(manager instanceof InMemoryTaskManager, "Возвращается некорректный тип менеджера");
    }

    @Test
    void shouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер истории не инициализирован");
        assertTrue(historyManager instanceof InMemoryHistoryManager, "Возвращается некорректный тип менеджера истории");
    }
}
