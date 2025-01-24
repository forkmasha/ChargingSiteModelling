package distributions;

public enum DistributionType {

    DETERMINISTIC,
    EXPONENTIAL,
    ERLANG,
    ERLANGD,
    BETA,
    UNIFORM,
    GEOMETRIC,
    LOMAX;

    public static DistributionType fromString(Object type){
        return fromString(type.toString());
    }

    public static DistributionType fromString(String type) {
        switch (type.toUpperCase()) {
            case "GEOMETRIC":
                return DistributionType.GEOMETRIC;
            case "EXPONENTIAL":
                return DistributionType.EXPONENTIAL;
            case "ERLANG":
                return DistributionType.ERLANG;
            case "ERLANGD":
                return DistributionType.ERLANGD;
            case "UNIFORM":
                return DistributionType.UNIFORM;
            case "BETA":
                return DistributionType.BETA;
            case "DETERMINISTIC":
                return DistributionType.DETERMINISTIC;
            case "LOMAX":
                return DistributionType.LOMAX;
            default:
                return null;
        }
    }
}