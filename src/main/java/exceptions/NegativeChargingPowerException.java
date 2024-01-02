package exceptions;

public class NegativeChargingPowerException extends RuntimeException {
    public NegativeChargingPowerException(String message) {
        super(message);
    }
}
