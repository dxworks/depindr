package depindr.exceptions;

public class DepinderException extends RuntimeException {
    public DepinderException(String s) {
        super(s);
    }

    public DepinderException(String message, Throwable cause) {
        super(message, cause);
    }
}
