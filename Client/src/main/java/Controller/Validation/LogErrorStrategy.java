package Controller.Validation;

public class LogErrorStrategy implements ErrorStrategy {
    @Override
    public void handleError(String message) {
        System.err.println(message);
    }
}