package ya.sp4.taskmanagerapp;

import ya.sp4.taskmanagerapp.manager.TaskManager;
import ya.sp4.taskmanagerapp.task.Epic;
import ya.sp4.taskmanagerapp.task.Subtask;
import ya.sp4.taskmanagerapp.task.Task;
import ya.sp4.taskmanagerapp.task.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = manager.createTask("Переезд", "Переезд в офис");
        Task task2 = manager.createTask("Закупка", "Канцелярия");
        Task task3 = manager.createTask("Найти", "Дизайнера");
        Task task4 = manager.createTask("Магазин", "Хлеба");
        Task task5 = manager.createTask("Квартира", "Счетчики");

        Epic epic1 = manager.createEpic("Эпик Ремонт", "Ремонт в новом офисе");
        Subtask subtask_ep1_1 = manager.createSubtask("Выбор подрядчика", "Найти подрядчика для ремонта", epic1.getId());
        Subtask subtask_ep1_2 = manager.createSubtask("Закупка материалов", "Закупить материалы для ремонта", epic1.getId());
        Subtask subtask_ep1_3 = manager.createSubtask("Выбор мебельного", "Найти мебель", epic1.getId());

        Epic epic2 = manager.createEpic("Эпик Техника", "Выбрать технику в квартиру");
        Subtask subtask_ep2_1 = manager.createSubtask("Техника в кухню", "Найти холодильник", epic2.getId());
        Subtask subtask_ep2_2 = manager.createSubtask("Техника в кухню", "Найти плиту", epic2.getId());
        Subtask subtask_ep2_3 = manager.createSubtask("Техника в кухню", "Найти посудомойку", epic2.getId());
        Subtask subtask_ep2_4 = manager.createSubtask("Техника в спальня", "Найти ночник", epic2.getId());
        Subtask subtask_ep2_5 = manager.createSubtask("Техника в гостиную", "Найти ТВ", epic2.getId());

        System.out.println("2.a. Получение списка всех задач");
        System.out.println("manager.printAllTasks");
        manager.printAllTasks();

        System.out.println("2.с. Получение по идентификатору.");
        System.out.println("Задачи:");
        System.out.println(manager.getTask(task1.getId()));
        System.out.println(manager.getTask(task2.getId()));
        System.out.println(manager.getTask(task3.getId()));
        System.out.println(manager.getTask(task4.getId()));
        System.out.println(manager.getTask(task5.getId()));
        System.out.println("------------------------------------");
        System.out.println("Эпики:");
        System.out.println(manager.getEpic(epic1.getId()));
        System.out.println(manager.getEpic(epic2.getId()));
        System.out.println("------------------------------------");
        System.out.println("Подзадачи: epic1");
        System.out.println(manager.getSubtask(subtask_ep1_1.getId()));
        System.out.println(manager.getSubtask(subtask_ep1_2.getId()));
        System.out.println(manager.getSubtask(subtask_ep1_3.getId()));
        System.out.println("------------------------------------");
        System.out.println("Подзадачи: epic2-printSubtasksByEpicId");
        manager.printSubtasksByEpicId(epic2.getId());
        System.out.println("2.d. Создание.");
        Task task = new Task(22, "Тест", "Тест создания", TaskStatus.NEW);
        manager.createTask(task);
        System.out.println(manager.getTask(task.getId()));
        System.out.println("2.e. Обновление.");
        Task updateTask = new Task(22, "Тест", "Тест обновления", TaskStatus.IN_PROGRESS);
        manager.updateTask(updateTask);
        System.out.println(manager.getTask(updateTask.getId()));
        System.out.println("2.f. Удаление по идентификатору.");
        manager.deleteTask(task.getId());
        System.out.println(manager.getTask(updateTask.getId()));
        System.out.println("3.с. Получение списка всех подзадач определённого эпика.");
        manager.printSubtasksByEpicId(epic1.getId());
        System.out.println("4. Статусы.");
        System.out.println("Статус эпика:");
        System.out.println(manager.getEpic(epic1.getId()));
        System.out.println(manager.getEpic(epic2.getId()));
        System.out.println("Меняем статусы");
        subtask_ep1_1.setStatus(TaskStatus.DONE);
        subtask_ep1_2.setStatus(TaskStatus.DONE);
        subtask_ep1_3.setStatus(TaskStatus.DONE);
        manager.updateEpicStatus(epic1.getId());

        subtask_ep2_3.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateEpicStatus(epic2.getId());

        System.out.println("Обновленный статус эпика:");
        System.out.println(manager.getEpic(epic1.getId()));
        System.out.println(manager.getEpic(epic2.getId()));

        System.out.println("2.b. Удаление всех задач.");
        System.out.println("manager.deleteAllTasks()");
        manager.deleteAllTasks();
        System.out.println("------------------------------------");
        System.out.println("manager.printAllTasks");
        manager.printAllTasks();
    }
}
