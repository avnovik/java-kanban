package ya.hw.taskmanagerapp.manager.impl;

import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.manager.exception.ManagerSaveException;
import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Реализация FileTaskManager с хранением данных в файле.
 * Автоматически сохраняет/обновляет/удаляет задачи в файле.
 * Восстанавливает данные менеджера из файла при запуске программы.
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    static private final String pathFileForHistory = "src/main/resources/history.csv";
    static private final String firstLine = "id,type,name,status,description,epic\n";
    private final Path file;

    public FileBackedTaskManager(Path file) {
        super(Managers.getDefaultHistory());
        this.file = file;
    }

    private void save() {
        try {
            StringBuilder data = new StringBuilder();
            data.append(firstLine);

            for (Epic epic : getAllEpics()) {
                data.append(taskToCsv(epic)).append("\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                data.append(taskToCsv(subtask)).append("\n");
            }

            for (Task task : getAllTasks()) {
                data.append(taskToCsv(task)).append("\n");
            }

            Files.writeString(file, data);
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить", e);
        }
    }

    private String taskToCsv(Task task) {
        String type = task.getType().name();

        //Получаем epicId (только для подзадач)
        String epicId = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                epicId);
    }


    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxIdCounterInFile = 0;
        try {
            List<String> lines = Files.readAllLines(file);

            for (String line : lines) {
                if (line.startsWith("id")) {
                    continue;
                }
                Task task = parseCsvLine(line);

                if (task != null) {
                    if (task.getType() == TaskType.TASK) {
                        manager.tasks.put(task.getId(), task);
                    } else if (task.getType() == TaskType.EPIC) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task.getType() == TaskType.SUBTASK) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                    }
                    if (task.getId() > maxIdCounterInFile) {
                        maxIdCounterInFile = task.getId();
                    }
                }
                for (Subtask subtask : manager.subtasks.values()) {
                    Epic epic = manager.epics.get(subtask.getEpicId());
                    if (epic != null) {
                        if (!epic.getSubtaskIds().contains(subtask.getId())) {
                            epic.addSubtaskId(subtask.getId());
                        }
                    }
                }
            }
            manager.idCounter = maxIdCounterInFile;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки", e);
        }
        return manager;
    }

    private static Task parseCsvLine(String csvLine) {
        String[] parts = csvLine.split(",");
        System.out.println();
        try {
            int id = Integer.parseInt(parts[0].trim());
            TaskType type = TaskType.valueOf(parts[1].trim());
            String title = parts[2].trim();
            TaskStatus status = TaskStatus.valueOf(parts[3].trim());
            String description = parts[4].trim();

            switch (type) {
                case TASK:
                    return new Task(id, title, description, status);
                case EPIC:
                    Epic epic = new Epic(id, title, description);
                    epic.setStatus(status);
                    return epic;
                case SUBTASK:
                    int epicId = parts.length > 5 ? Integer.parseInt(parts[5].trim()) : -1;
                    return new Subtask(id, title, description, status, epicId);
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Ошибка парсинга строки в parseCsvLine: " + csvLine);
            return null;
        }
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }


    public static void main(String[] args) {
        Path file = Path.of(pathFileForHistory);
        TaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task(1, "Помыть посуду", "Срочно!", TaskStatus.NEW);
        Task task2 = new Task(2, "Купить продукты", "Молоко, хлеб", TaskStatus.NEW);

        Epic epicWithSubtasks = new Epic(3, "Переезд", "Организация переезда");
        Subtask subtask1 = new Subtask(4, "Упаковать вещи", "Коробки", TaskStatus.NEW, 3);
        Subtask subtask2 = new Subtask(5, "Нанять грузчиков", "", TaskStatus.DONE, 3);
        Subtask subtask3 = new Subtask(6, "Заказать фургон", "Газель", TaskStatus.IN_PROGRESS, 3);

        Epic epicWithoutSubtasks = new Epic(7, "Пустой эпик", "Без подзадач");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epicWithSubtasks);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        manager.createEpic(epicWithoutSubtasks);


        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Задачи: " + loadManager.getAllTasks());
        System.out.println("Эпики: " + loadManager.getAllEpics());
        System.out.println("Подзадачи: " + loadManager.getAllSubtasks());
    }
}
