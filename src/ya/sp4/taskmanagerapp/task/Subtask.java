package ya.sp4.taskmanagerapp.task;

public class Subtask extends Task{
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
}
