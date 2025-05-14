package ya.hw.taskmanagerapp.manager.impl;

import main.ya.hw.taskmanagerapp.manager.TaskManager;
import main.ya.hw.taskmanagerapp.manager.impl.FileBackedTaskManager;
import main.ya.hw.taskmanagerapp.task.Epic;
import main.ya.hw.taskmanagerapp.task.Subtask;
import main.ya.hw.taskmanagerapp.task.Task;
import main.ya.hw.taskmanagerapp.task.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    @Test
    @DisplayName("Проверка сохранения и загрузки пустого файла")
    void shouldSaveAndLoadEmptyManager() {
        try {
            File tmpFile = createTempFile("shouldSaveAndLoadEmptyManager_", ".csv", new File("src/main/resources/"));
            TaskManager manager = new FileBackedTaskManager(tmpFile.toPath());
            TaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile.toPath());

            tmpFile.deleteOnExit();


            assertTrue(loaded.getAllTasks().isEmpty());
        } catch (IOException e) {
            System.err.println("Ошибка создания/удаления файла в shouldSaveAndLoadEmptyManager");
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Проверка сохранения и загрузки одной задачи")
    void shouldSaveAndLoadTasks() {
        try {
            File tmpFile = createTempFile("shouldSaveAndLoadTasks_", ".csv", new File("src/main/resources/"));
            TaskManager manager = new FileBackedTaskManager(tmpFile.toPath());

            manager.createTask(new Task(1, "Task", "SomeTask", TaskStatus.NEW));
            TaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile.toPath());

            tmpFile.deleteOnExit();
            assertEquals(1, loaded.getAllTasks().size());
        } catch (IOException e) {
            System.err.println("Ошибка создания/удаления файла в shouldSaveAndLoadTasks");
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Проверка сохранения и загрузки нескольких задач")
    void shouldSaveAndLoadSomeTasks() {
        try {
            File tmpFile = createTempFile("shouldSaveAndLoadTasks_", ".csv", new File("src/main/resources/"));
            TaskManager manager = new FileBackedTaskManager(tmpFile.toPath());
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

            TaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile.toPath());
            int sumAllTypeTasks = loaded.getAllTasks().size() + loaded.getAllSubtasks().size() + loaded.getAllEpics().size();
            tmpFile.deleteOnExit();
            assertEquals(7, sumAllTypeTasks);
        } catch (IOException e) {
            System.err.println("Ошибка создания/удаления файла в shouldSaveAndLoadSomeTasks");
            e.printStackTrace();
        }
    }
}
