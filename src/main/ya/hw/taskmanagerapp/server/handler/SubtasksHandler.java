package ya.hw.taskmanagerapp.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.manager.exception.ManagerValidateException;
import ya.hw.taskmanagerapp.task.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method) && path.equals("/subtasks")) {
                handleGetAllSubtasks(exchange); // Обработка GET /subtasks
            } else if ("GET".equals(method) && path.matches("/subtasks/\\d+")) {
                handleGetSubtaskById(exchange); // Обработка GET /subtasks/{id}
            } else if ("POST".equals(method) && path.equals("/subtasks")) {
                handleCreateOrUpdateSubtask(exchange); // Обработка POST /subtasks
            } else if ("DELETE".equals(method) && path.matches("/subtasks/\\d+")) {
                handleDeleteSubtask(exchange); // Обработка DELETE /subtasks/{id}
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = manager.getAllSubtasks();
        sendText(exchange, gson.toJson(subtasks), 200);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        Subtask subtask = manager.getSubtask(id);
        if (subtask == null) {
            sendNotFound(exchange);
        } else {
            sendText(exchange, gson.toJson(subtask), 200);
        }
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String requestBody = parseJsonBody(exchange);
        if (requestBody == null) return;

        Subtask subtask = gson.fromJson(requestBody, Subtask.class);
        try {
            if (subtask.getId() == 0) {
                int id = manager.createSubtask(subtask);
                sendText(exchange, "{\"id\":" + id + "}", 201);
            } else {
                manager.updateSubtask(subtask);
                sendText(exchange, "{\"message\":\"Subtask updated\"}", 200);
            }
        } catch (ManagerValidateException e) {
            sendText(exchange, "{\"error\":\"Пересечение времени\"}", 406);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        manager.deleteSubtask(id);
        sendText(exchange, "{\"message\":\"Subtask deleted\"}", 200);
    }

    private int parseId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return Integer.parseInt(path.split("/")[2]);
    }
}
