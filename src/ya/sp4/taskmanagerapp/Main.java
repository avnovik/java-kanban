package ya.sp4.taskmanagerapp;

import ya.sp4.taskmanagerapp.manager.TaskManager;
import ya.sp4.taskmanagerapp.task.Epic;
import ya.sp4.taskmanagerapp.task.Subtask;
import ya.sp4.taskmanagerapp.task.Task;
import ya.sp4.taskmanagerapp.task.TaskStatus;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        System.out.println("2.d. Создание.");
        Task task1 = new Task(22, "Тест", "Тест создания", TaskStatus.NEW);
        int idTask1 = manager.createTask(task1);
        Task task2 = new Task(22, "Тест", "Тест создания", TaskStatus.NEW);
        int idTask2 = manager.createTask(task2);
        Task task3 = new Task(22, "Тест", "Тест создания", TaskStatus.NEW);
        int idTask3 = manager.createTask(task3);

        Epic epic1 = new Epic(22, "Эпик1 Тест", "Эпик1 Тест создания");
        int idEpicTask1 = manager.createEpic(epic1);
        Epic epic2 = new Epic(22, "Эпик2 Тест", "Эпик2 Тест создания");
        int idEpicTask2 = manager.createEpic(epic2);

        Subtask subtask1 = new Subtask(22,"ПодТест1", "ПодТест создания", TaskStatus.NEW, idEpicTask1);
        int subtaskId1 = manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask(22,"ПодТест2", "ПодТест создания", TaskStatus.NEW, idEpicTask1);
        int subtaskId2 = manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask(22,"ПодТест3", "ПодТест создания", TaskStatus.NEW, idEpicTask1);
        int subtaskId3 = manager.createSubtask(subtask3);

        Subtask subtask2_1 = new Subtask(22,"ПодТест2-1", "ПодТест создания", TaskStatus.NEW, idEpicTask2);
        int subtaskId2_1 = manager.createSubtask(subtask2_1);
        Subtask subtask2_2 = new Subtask(22,"ПодТест2-2", "ПодТест создания", TaskStatus.NEW, idEpicTask2);
        int subtaskId2_2 = manager.createSubtask(subtask2_2);
        Subtask subtask2_3 = new Subtask(22,"ПодТест2-3", "ПодТест создания", TaskStatus.NEW, idEpicTask2);
        int subtaskId2_3 = manager.createSubtask(subtask2_3);
//      проверка - эпик не сущ
//        Subtask subtask2_4 = new Subtask(22,"ПодТест2-3", "ПодТест создания", TaskStatus.NEW, 123);

        System.out.println("2.с. Получение по идентификатору.");
        System.out.println("Задачи:");
        System.out.println(manager.getTask(idTask1));
        System.out.println(manager.getTask(idTask2));
        System.out.println(manager.getTask(idTask3));
        System.out.println("------------------------------------");
        System.out.println("Эпики:");
        System.out.println(manager.getEpic(idEpicTask1));
        System.out.println(manager.getEpic(idEpicTask2));
        System.out.println("------------------------------------");
        System.out.println("Подзадачи:");
        System.out.println(manager.getSubtask(subtaskId1));
        System.out.println(manager.getSubtask(subtaskId2));
        System.out.println(manager.getSubtask(subtaskId3));
        System.out.println(manager.getSubtask(subtaskId2_1));
        System.out.println(manager.getSubtask(subtaskId2_2));
        System.out.println(manager.getSubtask(subtaskId2_3));
        System.out.println("------------------------------------");

        System.out.println("2.e. Обновление.");
        System.out.println("### Задачи:");
        System.out.println("До " + manager.getTask(idTask1));
        Task updateTask = new Task(idTask1, "Тест Тест", "Тест обновления Тест", TaskStatus.IN_PROGRESS);
        manager.updateTask(updateTask);
        System.out.println("После " + manager.getTask(idTask1));
        System.out.println("### Эпики:");
        System.out.println("До " + manager.getEpic(idEpicTask1));
        Epic epic = manager.getEpic(idEpicTask1);
        epic.setTitle("Эпик Новое название");
        epic.setDescription("Эпик Новое описание");
        manager.updateEpic(epic);
        System.out.println("После " + manager.getEpic(idEpicTask1));
        System.out.println("### Подзадачи:");
        System.out.println("До " + manager.getSubtask(subtaskId2));
        Subtask updateSubtask = new Subtask(subtaskId2,"ПодТест2 updateSubtask", "ПодТест создания updateSubtask", TaskStatus.IN_PROGRESS, idEpicTask1);
        manager.updateSubtask(updateSubtask);
        System.out.println("После " + manager.getSubtask(subtaskId2));

        System.out.println("2.a. Получение списка всех задач.");
        System.out.println("### Задачи:");
        List<Task> taskList = manager.getAllTasks();
        for (Task task: taskList) {
            System.out.println(task);
        }
        System.out.println("### Эпики:");
        List<Epic> epicsList = manager.getAllEpics();
        for (Epic task: epicsList) {
            System.out.println(task);
        }
        System.out.println("### Подзадачи:");
        List<Subtask> subtaskList = manager.getAllSubtasks();
        for (Subtask task: subtaskList) {
            System.out.println(task);
        }
        System.out.println("2.f. Удаление по идентификатору.");
        manager.deleteSubtask(subtask2_3.getId());
        System.out.println("3.a. Получение списка всех подзадач определённого эпика.");
        List<Subtask> subtaskListByEpicId = manager.getSubtasksByEpicId(idEpicTask2);
        for (Subtask task: subtaskListByEpicId) {
            System.out.println(task);
        }

        System.out.println("------------------------------------");
        System.out.println("2b. Удаление всех задач.");
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
    }
}
