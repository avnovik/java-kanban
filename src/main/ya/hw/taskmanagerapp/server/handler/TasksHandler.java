package ya.hw.taskmanagerapp.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.manager.exception.ManagerValidateException;
import ya.hw.taskmanagerapp.server.util.BaseHttpHandler;
import ya.hw.taskmanagerapp.task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            // Обработка GET /tasks
            if ("GET".equals(method) && path.equals("/tasks")) {
                System.out.println("GET + /tasks");
                handleGetAllTasks(exchange);
            }
            // Обработка GET /tasks/{id}
            else if ("GET".equals(method) && path.matches("/tasks/\\d+")) {
                handleGetTaskById(exchange);
            }
            // Обработка POST /tasks
            else if ("POST".equals(method) && path.equals("/tasks")) {
                handleCreateOrUpdateTask(exchange);
            }
            // Обработка DELETE /tasks/{id}
            else if ("DELETE".equals(method) && path.matches("/tasks/\\d+")) {
                handleDeleteTask(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, "{\"error\": \"Internal Server Error\"}", 500);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getAllTasks();
        String jsonResponse = gson.toJson(tasks);
        sendText(exchange, jsonResponse, 200);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        int id = parseTaskId(exchange);
        Task task = manager.getTask(id);

        if (task == null) {
            sendNotFound(exchange);
        } else {
            sendText(exchange, gson.toJson(task), 200);
        }
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(requestBody, Task.class);
        try {
            if (task.getId() == 0) {
                int id = manager.createTask(task);
                String responseJson = String.format("{\"message\":\"Задача создана\",\"id\":%d}", id);
                sendText(exchange, responseJson, 201);
            } else {
                manager.updateTask(task);
                sendText(exchange, "{\"message\": \"Задача обновлена\"}", 200);
            }
        } catch (ManagerValidateException e) {
            sendText(exchange, "{\"error\":\"Пересечение времени\"}", 406);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        int id = parseTaskId(exchange);
        manager.deleteTask(id);
        sendText(exchange, "{\"message\": \"Задача удалена\"}", 200);
    }

    private int parseTaskId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return Integer.parseInt(path.split("/")[2]);
    }
}