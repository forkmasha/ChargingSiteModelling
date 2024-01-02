package exceptions;

public class SitePowerExceededException extends RuntimeException {
    public SitePowerExceededException(String message) {
        super(message);
    }
}