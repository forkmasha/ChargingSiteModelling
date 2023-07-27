package distributions;

import java.util.Random;

public abstract class Distribution {
    private DistributionType type;
    private static Random random = new Random();

    public double getSample(double mean) {
        return 0;
    }

    public abstract double[] getSamples(double mean, int count);

    public abstract double[] getPDF(double mean, double xMax);

    public DistributionType getType() {
        return type;
    }

    public Distribution(DistributionType type) {
        this.type = type;
    }

    public static Distribution create(DistributionType type) {

        switch (type) {
            case GEOMETRIC -> {
                return new GeometricDistribution(DistributionType.GEOMETRIC);
            }
            case EXPONENTIAL -> {
                return new ExponentialDistribution(DistributionType.EXPONENTIAL);
            }
            case ERLANG -> {
                return new ErlangDistribution(DistributionType.ERLANG);
            }
            case UNIFORM -> {
                return new UniformDistribution(DistributionType.UNIFORM);
            }
            case BETA -> {
                return new BetaDistribution(DistributionType.BETA);
            }
            default -> {
                System.out.println("Warning: unsupported Distribution Type");
                return null;
            }

        }
    }
}