package main.ya.hw.taskmanagerapp.task;

public class Subtask extends Task{
    private final int epicId;

    public Subtask(int id, String tittle, String description, TaskStatus status, int epicId) {
        super(id, tittle, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
