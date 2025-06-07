package ya.hw.taskmanagerapp.manager.exception;

/**
 * Исключение для валидации задач (пересечение времени).
 */
public class ManagerValidateException extends RuntimeException {
    public ManagerValidateException(String message) {
        super(message);
    }
}