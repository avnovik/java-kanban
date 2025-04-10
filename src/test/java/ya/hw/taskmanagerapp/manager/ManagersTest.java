package ya.hw.taskmanagerapp.manager;

import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.manager.impl.InMemoryHistoryManager;
import ya.hw.taskmanagerapp.manager.impl.InMemoryTaskManager;

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
