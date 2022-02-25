package UI.exceptions;

public class WindowStateException extends Exception {
    public WindowStateException() {
        super();
    }

    public WindowStateException(String message) {
        super(message);
    }

    public WindowStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public WindowStateException(Throwable cause) {
        super(cause);
    }
}
