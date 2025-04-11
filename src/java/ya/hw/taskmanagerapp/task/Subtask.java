package ya.hw.taskmanagerapp.task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String tittle, String description, TaskStatus status, int epicId) {
        super(id, tittle, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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
