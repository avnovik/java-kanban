package ya.hw.taskmanagerapp.task;

import org.junit.jupiter.api.BeforeEach;
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
    void withNoSubtasks_returnsNew() {
        assertEquals(TaskStatus.NEW, epic.getStatus(),
                "Пустой эпик должен иметь статус NEW");
    }

    @Test
    void withAllNewSubtasks_returnsNew() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub1", "", TaskStatus.NEW, epicId));
        manager.createSubtask(new Subtask(3, "Sub2", "", TaskStatus.NEW, epicId));

        assertEquals(TaskStatus.NEW, manager.getEpic(epicId).getStatus());
    }

    @Test
    void withAllDoneSubtasks_returnsDone() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub1", "", TaskStatus.DONE, epicId));
        manager.createSubtask(new Subtask(3, "Sub2", "", TaskStatus.DONE, epicId));

        assertEquals(TaskStatus.DONE, manager.getEpic(epicId).getStatus());
    }

    @Test
    void withMixedStatusSubtasks_returnsInProgress() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub1", "", TaskStatus.NEW, epicId));
        manager.createSubtask(new Subtask(3, "Sub2", "", TaskStatus.DONE, epicId));

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epicId).getStatus());
    }

    @Test
    void withAllInProgressSubtasks_returnsInProgress() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub1", "", TaskStatus.IN_PROGRESS, epicId));
        manager.createSubtask(new Subtask(3, "Sub2", "", TaskStatus.IN_PROGRESS, epicId));

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpic(epicId).getStatus());
    }

    @Test
    void deleteEpic_removesAllSubtasks() {
        int epicId = manager.createEpic(epic);
        manager.createSubtask(new Subtask(2, "Sub", "", TaskStatus.NEW, epicId));

        manager.deleteEpic(epicId);
        assertTrue(manager.getAllSubtasks().isEmpty());
    }
}
