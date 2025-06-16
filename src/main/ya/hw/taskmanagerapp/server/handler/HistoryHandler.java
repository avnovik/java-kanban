package ya.hw.taskmanagerapp.server.handler;

import com.sun.net.httpserver.HttpExchange;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.server.util.BaseHttpHandler;
import ya.hw.taskmanagerapp.task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetHistory(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"Internal Server Error\"}", 500);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> history = manager.getHistory();
        sendText(exchange, gson.toJson(history), 200);
    }
}