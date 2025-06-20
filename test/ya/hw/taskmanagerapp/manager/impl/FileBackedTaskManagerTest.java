package ya.hw.taskmanagerapp.manager.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static final Path TEST_DIR = Paths.get("test/resources");
    private Path tempFile;

    @BeforeAll
    static void setup() {
        try {
            if (!Files.exists(TEST_DIR)) {
                Files.createDirectories(TEST_DIR);
            }
        } catch (IOException e) {
            fail("!!!DANGER!!! не смогли создать директорию в BeforeAll" + e.getMessage());
        }
    }

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            tempFile = Files.createTempFile(TEST_DIR, "test_", ".csv");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("!!!DANGER!!! не смогли создать файл в 'FileBackedTaskManagerTest.createManager'", e);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }

    @Test
    @DisplayName("Проверка сохранения и загрузки пустого файла")
    void shouldSaveAndLoadEmptyManager() {
        try {
            File tmpFile = createTempFile("shouldSaveAndLoadEmptyManager_", ".csv", TEST_DIR.toFile());
            TaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile.toPath());

            tmpFile.deleteOnExit();
            assertTrue(loaded.getAllTasks().isEmpty());
        } catch (IOException e) {
            fail("Ошибка создания/удаления файла в shouldSaveAndLoadEmptyManager" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Проверка сохранения и загрузки одной задачи")
    void shouldSaveAndLoadTasks() {
        try {
            File tmpFile = createTempFile("shouldSaveAndLoadTasks_", ".csv",
                    new File("test/resources/"));
            TaskManager manager = new FileBackedTaskManager(tmpFile.toPath());

            manager.createTask(new Task(1, "Task", "SomeTask", TaskStatus.NEW, null, null));
            TaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile.toPath());

            tmpFile.deleteOnExit();
            assertEquals(1, loaded.getAllTasks().size());
        } catch (IOException e) {
            fail("Ошибка создания/удаления файла в shouldSaveAndLoadTasks" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Проверка сохранения и загрузки нескольких задач")
    void shouldSaveAndLoadSomeTasks() {
        try {
            File tmpFile = createTempFile("shouldSaveAndLoadTasks_", ".csv",
                    new File("test/resources/"));
            TaskManager manager = new FileBackedTaskManager(tmpFile.toPath());
            Task task1 = new Task(1, "Помыть посуду", "Срочно!", TaskStatus.NEW, null, null);
            Task task2 = new Task(2, "Купить продукты", "Молоко и хлеб", TaskStatus.NEW, null, null);

            Epic epicWithSubtasks = new Epic(3, "Переезд", "Организация переезда");
            Subtask subtask1 = new Subtask(4, "Упаковать вещи", "Коробки", TaskStatus.NEW, 3, null, null);
            Subtask subtask2 = new Subtask(5, "Нанять грузчиков", "", TaskStatus.DONE, 3, null, null);
            Subtask subtask3 = new Subtask(6, "Заказать фургон", "Газель", TaskStatus.IN_PROGRESS, 3, null, null);

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
            fail("Ошибка создания/удаления файла в shouldSaveAndLoadSomeTasks" + e.getMessage());
        }
    }

    @Test
    @DisplayName("При загрузке из файла idCounter должен восстанавливаться как максимальный ID + 1")
    void shouldRestoreIdCounterAsMaxIdPlusOne() {
        try {
            String csvData = "id,type,name,status,description,startTime,duration,epic\n" +
                    "1,TASK,Task 1,NEW,Description 1, , \n" +
                    "5,EPIC,Epic 1,DONE,Description 2, , \n" +
                    "10,SUBTASK,Subtask 1,IN_PROGRESS,Description 3, , , 5";
            File tmpFile = createTempFile("shouldRestoreIdCounterAsMaxIdPlusOne_", ".csv",
                    new File("test/resources/"));
            Files.writeString(tmpFile.toPath(), csvData);

            FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile.toPath());

            Task newTask = new Task(-1, "New Task", "New Desc", TaskStatus.NEW, null, null);
            int newId = manager.createTask(newTask);

            tmpFile.deleteOnExit();
            assertEquals(11, newId, "ID новой задачи должен быть 11 (maxId + 1)");
        } catch (IOException e) {
            fail("Ошибка в shouldRestoreIdCounterAsMaxIdPlusOne" + e.getMessage());
        }
    }

    @Test
    @DisplayName("Проверка восстановления всех полей при загрузке задач с разными статусами")
    void shouldSaveAndLoadTasksWithDifferentStatuses() {
        File tmpFile = null;
        try {
            tmpFile = createTempFile("shouldSaveAndLoadEmptyManager_", ".csv", TEST_DIR.toFile());

            TaskManager manager = new FileBackedTaskManager(tmpFile.toPath());
            LocalDateTime taskStartTime = LocalDateTime.of(2024, 6, 10, 10, 0);
            Duration taskDuration = Duration.ofMinutes(30);
            Task doneTask = new Task(1, "Задача 1", "Описание", TaskStatus.DONE, taskStartTime, taskDuration);
            Epic inProgressEpic = new Epic(2, "Эпик", "Описание эпика");
            inProgressEpic.setStatus(TaskStatus.IN_PROGRESS);

            LocalDateTime subtaskStartTime = LocalDateTime.of(2024, 6, 11, 12, 0);
            Duration subtaskDuration = Duration.ofHours(2);
            Subtask newSubtask = new Subtask(3, "Подзадача", "Описание", TaskStatus.NEW, 2, subtaskStartTime, subtaskDuration);
            Subtask doneSubtask = new Subtask(4, "Подзадача 2", "Описание", TaskStatus.DONE, 2, null, null);

            manager.createTask(doneTask);
            manager.createEpic(inProgressEpic);
            manager.createSubtask(newSubtask);
            manager.createSubtask(doneSubtask);

            TaskManager loadedManager = FileBackedTaskManager.loadFromFile(tmpFile.toPath());

            Task loadedTask = loadedManager.getTask(1);
            assertEquals(doneTask.getStatus(), loadedTask.getStatus(), "Сравниваем значение поля 'Status' у TASK");
            assertEquals(doneTask.getTitle(), loadedTask.getTitle(), "Сравниваем значение поля 'Title' у TASK");
            assertEquals(doneTask.getDescription(), loadedTask.getDescription(), "Сравниваем значение поля 'Description' у TASK");
            assertEquals(doneTask.getStartTime(), loadedTask.getStartTime(), "StartTime TASK");
            assertEquals(doneTask.getDuration(), loadedTask.getDuration(), "Duration TASK");

            Epic loadedEpic = loadedManager.getEpic(2);
            assertEquals(inProgressEpic.getStatus(), loadedEpic.getStatus(), "Сравниваем значение поля 'Status' у EPIC");
            assertEquals(inProgressEpic.getTitle(), loadedEpic.getTitle(), "Сравниваем значение поля 'Title' у EPIC");
            assertEquals(inProgressEpic.getDescription(), loadedEpic.getDescription(), "Сравниваем значение поля 'Description' у EPIC");
            assertEquals(inProgressEpic.getSubtaskIds(), loadedEpic.getSubtaskIds(), "Сравниваем значение 'subtaskIds' у EPIC");
            assertEquals(inProgressEpic.getStartTime(), loadedEpic.getStartTime(), "StartTime EPIC");
            assertEquals(inProgressEpic.getDuration(), loadedEpic.getDuration(), "Duration EPIC");
            assertEquals(inProgressEpic.getEndTime(), loadedEpic.getEndTime(), "EndTime EPIC");

            Subtask loadedNewSubtask = loadedManager.getSubtask(3);
            assertEquals(newSubtask.getStatus(), loadedNewSubtask.getStatus(), "Сравниваем значение поля 'Status' у SUBTASK");
            assertEquals(newSubtask.getTitle(), loadedNewSubtask.getTitle(), "Сравниваем значение поля 'Title' у SUBTASK");
            assertEquals(newSubtask.getDescription(), loadedNewSubtask.getDescription(), "Сравниваем значение поля 'Description' у SUBTASK");
            assertEquals(newSubtask.getEpicId(), loadedNewSubtask.getEpicId(), "Сравниваем значение поля 'EpicId' у SUBTASK");
            assertEquals(newSubtask.getStartTime(), loadedNewSubtask.getStartTime(), "StartTime SUBTASK");
            assertEquals(newSubtask.getDuration(), loadedNewSubtask.getDuration(), "Duration SUBTASK");

            Subtask loadedDoneSubtask = loadedManager.getSubtask(4);
            assertEquals(doneSubtask.getStatus(), loadedDoneSubtask.getStatus(), "Статус DONE подзадачи должен сохраниться");
            assertEquals(doneSubtask.getTitle(), loadedDoneSubtask.getTitle(), "Сравниваем значение поля 'Title' у SUBTASK");
            assertEquals(doneSubtask.getDescription(), loadedDoneSubtask.getDescription(), "Сравниваем значение поля 'Description' у SUBTASK");
            assertEquals(doneSubtask.getEpicId(), loadedDoneSubtask.getEpicId(), "Сравниваем значение поля 'EpicId' у SUBTASK");
            assertEquals(doneSubtask.getStartTime(), loadedDoneSubtask.getStartTime(), "StartTime SUBTASK");
            assertEquals(doneSubtask.getDuration(), loadedDoneSubtask.getDuration(), "Duration SUBTASK");

        } catch (IOException e) {
            fail("Тест упал из-за IOException в shouldSaveAndLoadTasksWithDifferentStatuses");
        } finally {
            if (tmpFile != null) {
                tmpFile.deleteOnExit();
            }
        }
    }
}