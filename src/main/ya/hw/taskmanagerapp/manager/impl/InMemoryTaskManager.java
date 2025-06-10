package ya.hw.taskmanagerapp.manager.impl;

import ya.hw.taskmanagerapp.manager.HistoryManager;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.manager.exception.ManagerValidateException;
import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.Subtask;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(
                    Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private boolean hasTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .filter(existingTask -> !existingTask.equals(newTask))
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

        tasks.values().forEach(task -> {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        });

        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        System.out.println("Удаляем все [SUBTASK]");

        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });

        epics.values().forEach(epic -> {
            epic.removeAllSubtask();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic.getId());
        });

        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        System.out.println("Удаляем все [EPIC]");

        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });

        epics.values().forEach(epic -> {
            historyManager.remove(epic.getId());
        });

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
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
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

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(newId);

        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());

        return newId;
    }

    @Override
    public int createEpic(Epic epic) {
        int newId = ++idCounter;
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (hasTimeOverlap(updatedTask)) {
            throw new ManagerValidateException("Задача пересекается по времени с существующей!");
        }

        Task oldTask = tasks.get(updatedTask.getId());
        prioritizedTasks.remove(oldTask);

        tasks.put(updatedTask.getId(), updatedTask);
        if (updatedTask.getStartTime() != null) {
            prioritizedTasks.add(updatedTask);
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (hasTimeOverlap(updatedSubtask)) {
            throw new ManagerValidateException("Подзадача пересекается по времени с существующей!");
        }

        Subtask oldSubtask = subtasks.get(updatedSubtask.getId());
        prioritizedTasks.remove(oldSubtask);

        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        if (updatedSubtask.getStartTime() != null) {
            prioritizedTasks.add(updatedSubtask);
        }
        updateEpicStatus(updatedSubtask.getEpicId());
        updateEpicTime(updatedSubtask.getEpicId());
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
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                historyManager.remove(id);

                updateEpicStatus(epic.getId());
                updateEpicTime(epic.getId());
            }
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
                prioritizedTasks.remove(subtask);
            }
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        List<Subtask> subtasksList;
        subtasksList = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return subtasksList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> subtasks = getSubtasksByEpicId(epicId);

        updateEpicTime(epicId);

        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = subtasks.stream()
                .allMatch(s -> s.getStatus() == TaskStatus.DONE);

        boolean allNew = subtasks.stream()
                .allMatch(s -> s.getStatus() == TaskStatus.NEW);

        epic.setStatus(
                allDone ? TaskStatus.DONE :
                allNew ? TaskStatus.NEW :
                TaskStatus.IN_PROGRESS
        );
    }

    protected void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> subtasks = getSubtasksByEpicId(epicId);

        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }

        LocalDateTime startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        Duration duration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }
}
