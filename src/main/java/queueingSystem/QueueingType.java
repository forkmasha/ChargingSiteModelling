package queueingSystem;

public enum QueueingType {
    FIFO,
    LIFO,
    RAND;

    public static QueueingType fromString(Object type) {
        return fromString(type.toString());
    }

    public static QueueingType fromString(String type) {
        return switch (type.toUpperCase()) {
            case "FIFO" -> QueueingType.FIFO;
            case "LIFO" -> QueueingType.LIFO;
            case "RAND" -> QueueingType.RAND;
            default -> throw new IllegalArgumentException("Unknown queueing type: " + type);
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case FIFO -> "FIFO";
            case LIFO -> "LIFO";
            case RAND -> "RAND";
        };
    }
}
