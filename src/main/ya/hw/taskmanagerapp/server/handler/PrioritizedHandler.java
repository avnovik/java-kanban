package ya.hw.taskmanagerapp.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.task.Task;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetPrioritizedTasks(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = manager.getPrioritizedTasks();
        sendText(exchange, gson.toJson(prioritizedTasks), 200);
    }
}