package ya.hw.taskmanagerapp.manager;

import ya.hw.taskmanagerapp.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
