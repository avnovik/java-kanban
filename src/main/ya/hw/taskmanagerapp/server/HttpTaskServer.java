package ya.hw.taskmanagerapp.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.server.handler.*;
import ya.hw.taskmanagerapp.server.util.DurationAdapter;
import ya.hw.taskmanagerapp.server.util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager manager;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServer() throws IOException {
        this.manager = Managers.getDefault();
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        registerHandlers();
    }

    public TaskManager getManager() {
        return manager;
    }

    private void registerHandlers() {
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));
        httpServer.createContext("/subtasks", new SubtasksHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен\n");
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();

    }
}