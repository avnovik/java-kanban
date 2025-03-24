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
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private static int idCounter = 0;

    public Task createTask(String title, String description) {
        Task task = new Task(++idCounter, title, description, TaskStatus.NEW);
        tasks.put(task.getId(), task);
        return task;
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public Epic createEpic(String title, String description) {
        Epic epic = new Epic(++idCounter, title, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
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

    public Subtask createSubtask(String title, String description, int epicId) {
        Subtask subtask = new Subtask(++idCounter, title, description, TaskStatus.NEW, epicId);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epicId);
        }
        return subtask;
    }

    public void createSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
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

    public void printSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Не найден Эпик с Id: " + epicId);
        } else {
            for (int subtaskId : epic.getSubtaskIds()) {
                System.out.println(subtasks.get(subtaskId));
            }
        }
    }

    public void updateEpicStatus(int epicId) {
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

    public void printAllTasks() {
        if (epics.isEmpty() && subtasks.isEmpty() && tasks.isEmpty()) {
            System.out.println("###printAllTasks: не могу распечатать, список пуст!");
        }
        for (Epic epic : epics.values()) {
            System.out.println( "[EPIC] # " + epic.getTitle() );

            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    System.out.println("** " + subtask.getTitle());
                }
            }
            System.out.println("---");
        }
        if (!tasks.isEmpty()){
            System.out.println("Обычные задачи:");
            for (Task task : tasks.values()) {
                System.out.println(" - " + task.getTitle());
            }
        }
    }

    public void deleteAllTasks() {
        System.out.println("Удаляем все задачи.");
        epics.clear();
        subtasks.clear();
        tasks.clear();
        System.out.println("Список задач пуст");
    }
}
