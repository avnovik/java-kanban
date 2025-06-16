package ya.hw.taskmanagerapp.server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.server.adapter.DurationAdapter;
import ya.hw.taskmanagerapp.server.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {

    protected TaskManager manager;

    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        logRequest(h);
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
        logResponse(h, statusCode, text);
    }

    protected void logRequest(HttpExchange h) {
        String method = h.getRequestMethod();
        String path = h.getRequestURI().getPath();
        String query = h.getRequestURI().getQuery();

        System.out.printf("\n[REQUEST]\n--> [%s] %s%s\n",
                method,
                path,
                query != null ? "?" + query : ""
        );
    }

    protected void logResponse(HttpExchange h, int statusCode, String body) {
        String method = h.getRequestMethod();
        String path = h.getRequestURI().getPath();

        System.out.printf("\n[RESPONSE]\n<-- [%s] %s | Status: %d | Response: %s%n\n",
                method,
                path,
                statusCode,
                body.length() > 100 ? body.substring(0, 100) + "{...много букв...}" : body
        );
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Not Found\"}", 404);
    }

    protected void bodyIsEmpty(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Request body is empty\"}", 400);
    }

    protected String parseJsonBody(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (requestBody.isBlank()) {
            bodyIsEmpty(exchange);
            return null;
        }

        return requestBody;
    }
}
