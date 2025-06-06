package ya.hw.taskmanagerapp.manager.impl;

import ya.hw.taskmanagerapp.manager.HistoryManager;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.manager.exception.ManagerValidateException;
import ya.hw.taskmanagerapp.task.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Реализация TaskManager с хранением данных в памяти.
 * Автоматически обновляет историю просмотров через HistoryManager.
 */
public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected int idCounter = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private boolean hasTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isTasksOverlap(newTask, existingTask));
    }

    private boolean isTasksOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        return task1.getEndTime().isAfter(task2.getStartTime())
                && task2.getEndTime().isAfter(task1.getStartTime());
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        Comparator<Task> taskComparator = Comparator.comparing(
                Task::getStartTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        );

        Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

        prioritizedTasks.addAll(getAllTasks().stream()
                .filter(task -> task.getStartTime() != null)
                .toList());

        prioritizedTasks.addAll(getAllSubtasks().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .toList());

        return prioritizedTasks;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        System.out.println("Удаляем все [TASK]");
        for (int taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();

    }

    @Override
    public void deleteAllSubtasks() {
        System.out.println("Удаляем все [SUBTASK]");
        for (int subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtask();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteAllEpics() {
        System.out.println("Удаляем все [EPIC]");
        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getSubtaskIds()) {
                historyManager.remove(subtaskId);
            }
        }
        for (int epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public int createTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new ManagerValidateException("Задача пересекается по времени с существующей!");
        }
        int newId = ++idCounter;
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            System.err.println("Ошибка: Эпик с ID=" + subtask.getEpicId() + " не найден");
            throw new IllegalArgumentException("Эпик не существует");
        }

        if (hasTimeOverlap(subtask)) {
            throw new ManagerValidateException("Подзадача пересекается по времени с существующей!");
        }

        int newId = ++idCounter;
        subtask.setId(newId);
        subtasks.put(newId, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(newId);
        updateEpicStatus(epic.getId());

        return newId;
    }

    @Override
    public int createEpic(Epic epic) {
        int newId = ++idCounter;
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }

    public LocalDateTime getEpicEndTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return null;
        List<Subtask> epicSubtasks = getSubtasksByEpicId(epicId);
        return epic.getEndTime(epicSubtasks);
    }

    @Override
    public void updateTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new ManagerValidateException("Задача пересекается по времени с существующей!");
        }

        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (hasTimeOverlap(newSubtask)) {
            throw new ManagerValidateException("Подзадача пересекается по времени с существующей!");
        }
        if (subtasks.containsKey(newSubtask.getId())) {
            subtasks.put(newSubtask.getId(), newSubtask);
            updateEpicStatus(newSubtask.getEpicId());
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = epics.get(updatedEpic.getId());
        if (existingEpic == null) {
            System.err.println("Ошибка: Эпик с ID=" + updatedEpic.getId() + " не найден");
            throw new IllegalArgumentException("Эпик не существует");
        }
        existingEpic.setTitle(updatedEpic.getTitle());
        existingEpic.setDescription(updatedEpic.getDescription());
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                historyManager.remove(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        }
    }

    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

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

        List<Subtask> epicSubtasks = getSubtasksByEpicId(epicId);
        epic.updateTime(epicSubtasks);
    }
}
