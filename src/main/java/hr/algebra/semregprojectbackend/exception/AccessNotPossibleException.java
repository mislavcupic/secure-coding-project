package hr.algebra.semregprojectbackend.exception;

public class AccessNotPossibleException extends RuntimeException {
    public AccessNotPossibleException(String message) {
        super(message);
    }
    public AccessNotPossibleException(String message, Throwable cause) {
        super(message, cause);
    }
    public AccessNotPossibleException(Throwable cause) {
        super(cause);
    }
}
