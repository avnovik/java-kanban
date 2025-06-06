package ya.hw.taskmanagerapp.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskIds;

    public Epic(int id, String tittle, String description) {
        super(id, tittle, description, TaskStatus.NEW, null, null);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void removeAllSubtask() {
        subtaskIds.clear();
    }

    public LocalDateTime getEndTime(List<Subtask> subtasksOfThisEpic) {
        if (subtasksOfThisEpic.isEmpty()) {
            return null;
        }
        return subtasksOfThisEpic.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public void updateTime(List<Subtask> subtasksOfThisEpic) {
        if (subtasksOfThisEpic.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            return;
        }

        this.startTime = subtasksOfThisEpic.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.duration = subtasksOfThisEpic.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
