package ya.hw.taskmanagerapp.manager;

import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.Subtask;
import ya.hw.taskmanagerapp.task.Task;

import java.util.List;

public interface TaskManager {
    /**
     * 2a. Получение списка всех задач.
     */
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    /**
     * 2b. Удаление всех задач.
     */
    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    /**
     * 2c. Получение по идентификатору.
     */
    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    /**
     * 2d. Создание. Сам объект должен передаваться в качестве параметра.
     */
    int createTask(Task task);

    int createSubtask(Subtask subtask);

    int createEpic(Epic epic);

    /**
     * 2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
     */
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic updatedEpic);

    /**
     * 2f. Удаление по идентификатору.
     */
    void deleteTask(int id);

    void deleteSubtask(int id);

    void deleteEpic(int id);

    /**
     * 3a. Получение списка всех подзадач определённого эпика.
     */
    List<Subtask> getSubtasksByEpicId(int epicId);

    /**
     * sp5. Показывает последние просмотренные пользователем задачи.
     */
    List<Task> getHistory();
}
