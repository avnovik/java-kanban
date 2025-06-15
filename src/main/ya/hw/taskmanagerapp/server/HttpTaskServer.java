package ya.hw.taskmanagerapp.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ya.hw.taskmanagerapp.manager.Managers;
import ya.hw.taskmanagerapp.manager.TaskManager;
import ya.hw.taskmanagerapp.server.handler.EpicsHandler;
import ya.hw.taskmanagerapp.server.handler.TasksHandler;
import ya.hw.taskmanagerapp.server.util.DurationAdapter;
import ya.hw.taskmanagerapp.server.util.LocalDateTimeAdapter;
import ya.hw.taskmanagerapp.task.Task;
import ya.hw.taskmanagerapp.task.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.Duration;

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

    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();

        Task task = new Task(1, "Test", "Description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));

    }
}