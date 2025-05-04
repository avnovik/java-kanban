package ya.hw.taskmanagerapp.manager.impl;

import ya.hw.taskmanagerapp.manager.HistoryManager;
import ya.hw.taskmanagerapp.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedHashMap<Integer, Task> historyMap = new LinkedHashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) return;
        historyMap.put(task.getId(), task);
    }

    @Override
    public void remove(int id) {
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyMap.values());
    }
}