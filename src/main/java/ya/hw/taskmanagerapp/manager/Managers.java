package ya.hw.taskmanagerapp.manager;

import ya.hw.taskmanagerapp.manager.impl.InMemoryHistoryManager;
import ya.hw.taskmanagerapp.manager.impl.InMemoryTaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
