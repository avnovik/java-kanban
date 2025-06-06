package ya.hw.taskmanagerapp.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {
    private final Subtask subtask = new Subtask(1, "Subtask", "Description", TaskStatus.NEW, 2, null, null);
    private final Subtask sameIdSubtask = new Subtask(1, "Different", "Another desc", TaskStatus.DONE, 2, null, null);
    private final Subtask differentIdSubtask = new Subtask(2, "Subtask", "Description", TaskStatus.NEW, 2, null, null);

    @Test
    void equals_returnsTrueForSameId() {
        assertEquals(subtask, sameIdSubtask, "Подзадачи с одинаковым ID должны быть равны");
    }

    @Test
    void equals_returnsFalseForDifferentIds() {
        assertNotEquals(subtask, differentIdSubtask, "Подзадачи с разными ID не должны быть равны");
    }
}
