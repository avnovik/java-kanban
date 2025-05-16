package ya.hw.taskmanagerapp.manager;

import ya.hw.taskmanagerapp.manager.impl.InMemoryHistoryManager;
import ya.hw.taskmanagerapp.manager.impl.InMemoryTaskManager;

/**
 * Фабрика для создания менеджеров задач.
 * Предоставляет готовые реализации TaskManager и HistoryManager.
 */
public class Managers {
    /**
     * Возвращает TaskManager с хранением данных в памяти (RAM).
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    /**
     * Возвращает HistoryManager с хранением истории в памяти.
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
