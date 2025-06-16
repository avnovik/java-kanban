package ya.hw.taskmanagerapp.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.task.Epic;
import ya.hw.taskmanagerapp.task.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method) && path.equals("/epics")) {
                handleGetAllEpics(exchange); // Обработка GET /epics
            } else if ("GET".equals(method) && path.matches("/epics/\\d+")) {
                handleGetEpicById(exchange); // Обработка GET /epics/{id}
            } else if ("GET".equals(method) && path.matches("/epics/\\d+/subtasks")) {
                handleGetEpicSubtasks(exchange); // Обработка GET /epics/{id}/subtasks
            } else if ("POST".equals(method) && path.equals("/epics")) {
                handleCreateEpic(exchange); // Обработка POST /epics
            } else if ("DELETE".equals(method) && path.matches("/epics/\\d+")) {
                handleDeleteEpic(exchange); // Обработка DELETE /epics/{id}
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getAllEpics();
        sendText(exchange, gson.toJson(epics), 200);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        Epic epic = manager.getEpic(id);
        if (epic == null) {
            sendNotFound(exchange);
        } else {
            sendText(exchange, gson.toJson(epic), 200);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        int epicId = parseId(exchange);
        if (manager.getEpic(epicId) == null) {
            sendNotFound(exchange);
            return;
        }
        List<Subtask> subtasks = manager.getSubtasksByEpicId(epicId);
        sendText(exchange, gson.toJson(subtasks), 200);
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        String requestBody = parseJsonBody(exchange);
        if (requestBody == null) return;

        Epic epic = gson.fromJson(requestBody, Epic.class);
        int id = manager.createEpic(epic);
        sendText(exchange, "{\"id\":" + id + "}", 201);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        int id = parseId(exchange);
        manager.deleteEpic(id);
        sendText(exchange, "{\"message\":\"Epic deleted\"}", 200);
    }

    private int parseId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return Integer.parseInt(path.split("/")[2]);
    }
}