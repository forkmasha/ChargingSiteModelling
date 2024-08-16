package chargingSite;

public class GuiParameters{
    public static String defaultQueueingType = "FIFO";
    public static String[] queueingTypes = {"FIFO", "LIFO", "RANDOM"};

    public static String defaultArrivalType = "EXPONENTIAL";
    public static String[] arrivalDistributionTypes = {"DETERMINISTIC", "EXPONENTIAL", "ERLANG", "UNIFORM", "LOMAX"};

    public static String defaultServiceType = "ERLANG";
    public static String[] serviceDistributionTypes = {"DETERMINISTIC", "EXPONENTIAL", "ERLANG", "ERLANGD", "UNIFORM", "LOMAX"};

    public static String defaultDemandType = "BETA";
    public static String[] demandDistributionTypes = {"DETERMINISTIC", "UNIFORM", "BETA"};
    
    public static String defaultConfidenceLevel = "95";
    public static String[] confidenceLevels = {"80", "90", "95", "98", "99"};

}