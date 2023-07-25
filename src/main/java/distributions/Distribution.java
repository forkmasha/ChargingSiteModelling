package distributions;
public abstract class Distribution {
    private DistributionType type;
    public double getSample(double mean) {
        return 0;
    }
    public abstract double[] getSamples(double mean, int count);
    public abstract double[] getPDF(double mean, double xMax);

    public static Distribution create( DistributionType type) {

        switch (type) {
            case GEOMETRIC -> {
                return new GeometricDistribution();
            }
            case EXPONENTIAL -> {
                return new ExponentialDistribution();
            }
            case ERLANG -> {
                return new ErlangDistribution();
            }
            case UNIFORM -> {
                return new UniformDistribution();
            }
            case BETA -> {
                return new BetaDistribution();
            }
            default -> {
                return null;
            }

        }
    }
}