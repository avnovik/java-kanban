package ya.hw.taskmanagerapp.task;

import main.ya.hw.taskmanagerapp.manager.Managers;
import main.ya.hw.taskmanagerapp.manager.TaskManager;
import main.ya.hw.taskmanagerapp.task.Task;
import main.ya.hw.taskmanagerapp.task.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TaskTest {
    private final Task task = new Task(1, "Task", "Description", TaskStatus.NEW);
    private final Task sameIdTask = new Task(1, "Different", "Another desc", TaskStatus.DONE);
    private final Task differentIdTask = new Task(2, "Task", "Description", TaskStatus.NEW);

    @Test
    @DisplayName("Задачи с одинаковым ID должны считаться равными, даже если другие поля отличаются")
    void equals_returnsTrueForSameId() {
        assertEquals(task, sameIdTask, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    @DisplayName("Задачи с разными ID не должны считаться равными")
    void equals_returnsFalseForDifferentIds() {
        assertNotEquals(task, differentIdTask, "Задачи с разными ID не должны быть равны");
    }

    @Test
    @DisplayName("Сравнение задач: равны при одинаковом ID, независимо от других атрибутов")
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task(1, "Task 1", "Description", TaskStatus.NEW);
        Task task2 = new Task(1, "Task 2", "Another desc", TaskStatus.DONE);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    @DisplayName("Сеттеры должны корректно изменять все поля задачи")
    void setters_changeTaskFields() {
        Task task = new Task(1, "Original", "Desc", TaskStatus.NEW);

        task.setTitle("New Title");
        task.setDescription("New Desc");
        task.setStatus(TaskStatus.DONE);

        assertEquals("New Title", task.getTitle());
        assertEquals("New Desc", task.getDescription());
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    @DisplayName("Добавление задачи в менеджер не должно изменять её содержимое")
    void taskShouldNotChangeAfterAddingToManager() {
        TaskManager manager = Managers.getDefault();
        Task originalTask = new Task(0, "Original", "Desc", TaskStatus.NEW);

        int taskId = manager.createTask(originalTask);
        Task savedTask = manager.getTask(taskId);

        assertEquals(originalTask.getTitle(), savedTask.getTitle(), "Название задачи изменилось");
        assertEquals(originalTask.getDescription(), savedTask.getDescription(), "Описание задачи изменилось");
        assertEquals(originalTask.getStatus(), savedTask.getStatus(), "Статус задачи изменился");
    }

    @Test
    @DisplayName("Изменение статуса задачи через сеттер")
    void taskStatusUpdateViaSetter() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task(1, "Task", "Desc", TaskStatus.NEW);
        manager.createTask(task);
        task.setStatus(TaskStatus.DONE);

        Task savedTask = manager.getTask(1);
        assertEquals(TaskStatus.DONE, savedTask.getStatus());
    }
}
