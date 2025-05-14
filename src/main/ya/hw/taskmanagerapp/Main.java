package main.ya.hw.taskmanagerapp;

import main.ya.hw.taskmanagerapp.manager.Managers;
import main.ya.hw.taskmanagerapp.manager.TaskManager;
import main.ya.hw.taskmanagerapp.task.Epic;
import main.ya.hw.taskmanagerapp.task.Subtask;
import main.ya.hw.taskmanagerapp.task.Task;
import main.ya.hw.taskmanagerapp.task.TaskStatus;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // 1. Создаем задачи
        Task task1 = new Task(1, "Помыть посуду", "Срочно!", TaskStatus.NEW);
        Task task2 = new Task(2, "Купить продукты", "Молоко, хлеб", TaskStatus.NEW);

        Epic epicWithSubtasks = new Epic(3, "Переезд", "Организация переезда");
        Subtask subtask1 = new Subtask(4, "Упаковать вещи", "Коробки", TaskStatus.NEW, 3);
        Subtask subtask2 = new Subtask(5, "Нанять грузчиков", "", TaskStatus.DONE, 3);
        Subtask subtask3 = new Subtask(6, "Заказать фургон", "Газель", TaskStatus.IN_PROGRESS, 3);

        Epic epicWithoutSubtasks = new Epic(7, "Пустой эпик", "Без подзадач");

        // 2. Добавляем задачи в менеджер
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epicWithSubtasks);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        manager.createEpic(epicWithoutSubtasks);

        // 3. Запрашиваем задачи в разном порядке
        System.out.println("=== Первые запросы ===");
        manager.getTask(1);
        manager.getEpic(3);
        manager.getSubtask(5);
        printHistory(manager);

        System.out.println("\n=== Повторные запросы (должны переместить задачи в конец) ===");
        manager.getTask(1);
        manager.getSubtask(6);
        printHistory(manager);

        // 4. Удаляем задачу из истории
        System.out.println("\n=== Удаляем задачу 5 ===");
        manager.deleteSubtask(5);
        printHistory(manager);

        // 5. Удаляем эпик с подзадачами
        System.out.println("\n=== Удаляем эпик 3 (должны удалиться и подзадачи 4,5,6) ===");
        manager.deleteEpic(3);
        printHistory(manager);
    }

    private static void printHistory(TaskManager manager) {
        List<Task> history = manager.getHistory();
        System.out.println("История просмотров:");
        if (history.isEmpty()) {
            System.out.println("[пусто]");
        } else {
            history.forEach(task -> System.out.println("- " + task));
        }
    }
}
