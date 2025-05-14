package ya.hw.taskmanagerapp.task;

import main.ya.hw.taskmanagerapp.task.Subtask;
import main.ya.hw.taskmanagerapp.task.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {
    private final Subtask subtask = new Subtask(1, "Subtask", "Description", TaskStatus.NEW, 2);
    private final Subtask sameIdSubtask = new Subtask(1, "Different", "Another desc", TaskStatus.DONE, 2);
    private final Subtask differentIdSubtask = new Subtask(2, "Subtask", "Description", TaskStatus.NEW, 2);

    @Test
    void equals_returnsTrueForSameId() {
        assertEquals(subtask, sameIdSubtask, "Подзадачи с одинаковым ID должны быть равны");
    }

    @Test
    void equals_returnsFalseForDifferentIds() {
        assertNotEquals(subtask, differentIdSubtask, "Подзадачи с разными ID не должны быть равны");
    }
}
