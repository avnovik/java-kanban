package ya.hw.taskmanagerapp.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicTest {
    private Epic epic;
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        epic = new Epic(1, "Test Epic", "Description");
    }

    @Test
    @DisplayName("Статус эпика без подзадач должен быть NEW")
    void withNoSubtasks_returnsNew() {
        assertEquals(TaskStatus.NEW, epic.getStatus(),
                "Пустой эпик должен иметь статус NEW");
    }

    @Test
    @DisplayName("Статус эпика со всеми подзадачами NEW должен быть NEW")
    void withAllNewSubtasks_returnsNew() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub1", "", TaskStatus.NEW, epicId));
        manager.createSubtask(new Subtask(3, "Sub2", "", TaskStatus.NEW, epicId));

        assertEquals(TaskStatus.NEW, manager.getEpic(epicId).getStatus());
    }

    @Test
    @DisplayName("Статус эпика со всеми подзадачами DONE должен быть DONE")
    void withAllDoneSubtasks_returnsDone() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub1", "", TaskStatus.DONE, epicId));
        manager.createSubtask(new Subtask(3, "Sub2", "", TaskStatus.DONE, epicId));

        assertEquals(TaskStatus.DONE, manager.getEpic(epicId).getStatus());
    }

    @Test
    @DisplayName("Статус эпика с подзадачами NEW и DONE должен быть IN_PROGRESS")
    void withMixedStatusSubtasks_returnsInProgress() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub1", "", TaskStatus.NEW, epicId));
        manager.createSubtask(new Subtask(3, "Sub2", "", TaskStatus.DONE, epicId));

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epicId).getStatus());
    }

    @Test
    @DisplayName("Статус эпика со всеми подзадачами IN_PROGRESS должен быть IN_PROGRESS")
    void withAllInProgressSubtasks_returnsInProgress() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub1", "", TaskStatus.IN_PROGRESS, epicId));
        manager.createSubtask(new Subtask(3, "Sub2", "", TaskStatus.IN_PROGRESS, epicId));

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epicId).getStatus());
    }

    @Test
    @DisplayName("Удаление эпика должно удалять все его подзадачи")
    void deleteEpic_removesAllSubtasks() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub", "", TaskStatus.NEW, epicId));

        manager.deleteEpic(epicId);
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    @DisplayName("Удаление подзадачи очищает её ID из Epic")
    void deleteSubtaskRemovesIdFromEpic() {
        Epic epic = new Epic(1, "Epic", "Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Subtask", "Desc", TaskStatus.NEW, 1);
        manager.createSubtask(subtask);

        manager.deleteSubtask(2);
        assertTrue(epic.getSubtaskIds().isEmpty(), "Подзадача не удалилась из Epic");
    }
}
