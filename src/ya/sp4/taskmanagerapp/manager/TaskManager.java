package ya.sp4.taskmanagerapp.manager;

import ya.sp4.taskmanagerapp.task.Epic;
import ya.sp4.taskmanagerapp.task.Subtask;
import ya.sp4.taskmanagerapp.task.Task;
import ya.sp4.taskmanagerapp.task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int idCounter = 0;

    /**
     *   2a. Получение списка всех задач.
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    /**
     *  2b. Удаление всех задач.
     */
    public void deleteAllTasks() {
        System.out.println("Удаляем все [TASK]");
        tasks.clear();
        System.out.println("Список задач пуст");
    }

    public void deleteAllSubtasks() {
        System.out.println("Удаляем все [SUBTASK]");
        subtasks.clear();
    }

    public void deleteAllEpics() {
        System.out.println("Удаляем все [EPIC]");
        epics.clear();
    }
    /**
     *  2c. Получение по идентификатору.
     */
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }
    /**
     *  2d. Создание. Сам объект должен передаваться в качестве параметра.
     */
    public int createTask(Task task) {
        int newId = ++idCounter;
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    public int createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            System.err.println("Ошибка: Эпик с ID=" + subtask.getEpicId() + " не найден");
            throw new IllegalArgumentException("Эпик не существует");
        }

        int newId = ++idCounter;
        subtask.setId(newId);
        subtasks.put(newId, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(newId);
            updateEpicStatus(epic.getId());
        }
        return newId;
    }

    public int createEpic(Epic epic) {
        int newId = ++idCounter;
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }
    /**
     *  2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
     */
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = epics.get(updatedEpic.getId());
        if (existingEpic == null) {
            System.err.println("Ошибка: Эпик с ID=" + updatedEpic.getId() + " не найден");
            throw new IllegalArgumentException("Эпик не существует");
        }
        existingEpic.setTitle(updatedEpic.getTitle());
        existingEpic.setDescription(updatedEpic.getDescription());
    }
    /**
     *  2f. Удаление по идентификатору.
     */
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }
    /**
     *  3a. Получение списка всех подзадач определённого эпика.
     */
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        List<Subtask> subtasksList = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                subtasksList.add(subtask);
            }
        }
        return subtasksList;
    }
    /**
     *  4b. Управление статусами эпиков
     */
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;

            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
