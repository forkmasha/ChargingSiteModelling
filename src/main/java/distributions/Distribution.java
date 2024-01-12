package distributions;

import java.util.Random;

public abstract class Distribution {
    private DistributionType type;
    private static Random random = new Random();

    public double getSample(double mean) {
        return 0;
    }

    public abstract double[] getSamples(double mean, int count);
    public  double[] getSamples(double mean, int count,double maxX){
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = getSample(mean);
            if(samples[i]>maxX){
                --i;
            }
        }
        System.out.println("WARNING: Generating a set of samples truncated to < " + maxX
                + " does not yield an exact " + this.getType().name() + " distribution.");
        return samples;
    };

    public double[][] getPDF(double mean, double xMax) {
        return new double[0][];
    }

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
            case ERLANGD -> {
                return new DiscreteErlangDistribution(DistributionType.ERLANGD);
            }
            case UNIFORM -> {
                return new UniformDistribution(DistributionType.UNIFORM);
            }
            case BETA -> {
                return new BetaDistribution(DistributionType.BETA);
            }
            case DETERMINISTIC -> {
                return new DetermanisticDistribution(DistributionType.DETERMINISTIC);
            }
            case LOMAX -> {
                return new LomaxDistribution(DistributionType.LOMAX);
            }
            default -> {
                System.out.println("Warning: unsupported Distribution Type");
                return null;
            }

        }
    }

    // Helper method to calculate the factorial of a number
    public static double factorial(int n) {
        double result = 1.0;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static String getTitleAbbreviation(String type) {
        if (type.equals("EXPONENTIAL")) {
            return "M";
        } else if (type.equals("ERLANG")) {
            //String type1 = "E"+ ErlangDistribution.level;
            return "E";
        } else if (type.equals("DETERMINISTIC")) {
            return "D";
        } else {
            return "G";
        }
    }

}