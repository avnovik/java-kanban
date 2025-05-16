package ya.hw.taskmanagerapp.manager.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.Subtask;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    @Test
    @DisplayName("Проверка сохранения и загрузки пустого файла")
    void shouldSaveAndLoadEmptyManager() {
        try {
            File tmpFile = createTempFile("shouldSaveAndLoadEmptyManager_", ".csv",
                    new File("src/main/resources/"));
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
            File tmpFile = createTempFile("shouldSaveAndLoadTasks_", ".csv",
                    new File("src/main/resources/"));
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
            File tmpFile = createTempFile("shouldSaveAndLoadTasks_", ".csv",
                    new File("src/main/resources/"));
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

    @Test
    @DisplayName("При загрузке из файла idCounter должен восстанавливаться как максимальный ID + 1")
    void shouldRestoreIdCounterAsMaxIdPlusOne() {
        try {
            String csvData = "id,type,name,status,description,epic\n" +
                    "1,TASK,Task 1,NEW,Description 1,\n" +
                    "5,EPIC,Epic 1,DONE,Description 2,\n" +
                    "10,SUBTASK,Subtask 1,IN_PROGRESS,Description 3,5";
            File tmpFile = createTempFile("shouldRestoreIdCounterAsMaxIdPlusOne_", ".csv",
                    new File("src/main/resources/"));
            Files.writeString(tmpFile.toPath(), csvData);

            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile.toPath());

            Task newTask = new Task(-1, "New Task", "New Desc", TaskStatus.NEW);
            int newId = manager.createTask(newTask);

            tmpFile.deleteOnExit();
            assertEquals(11, newId, "ID новой задачи должен быть 11 (maxId + 1)");
        } catch (IOException e) {
            System.err.println("Ошибка в shouldRestoreIdCounterAsMaxIdPlusOne");
            e.printStackTrace();
        }
    }
}