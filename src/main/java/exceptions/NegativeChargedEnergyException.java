package exceptions;

public class NegativeChargedEnergyException extends RuntimeException {
    public NegativeChargedEnergyException(String message) {
        super(message);
    }
}