package distributions;

import java.util.Random;

public class DiscreteErlangDistribution extends Distribution {

    public static final int level = 7;
    public static final double slice=0.25;// 15 minutes by default

    public DiscreteErlangDistribution(DistributionType type) {
        super(type);
    }

    public double getSample(double mean) {
        //Random random = new Random();
        double sample = 0;
        for (int i = 0; i < level; i++) {
            sample += exponentialDistribution(mean / slice - 1);
        }
        sample = Math.round(sample / level);
        return slice * (sample + 1);
    }

    public static double createSample(int level, double mean, double slice) {
        //Random random = new Random();
        double sample = 0;
        for (int i = 0; i < level; i++) {
            sample += exponentialDistribution(mean / slice);
        }
        sample = Math.round(sample / level);
        return slice * sample;
    }

    @Override
    public double[] getSamples(double mean, int count) {
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = getSample(mean);
        }
        return samples;
    }


    public double[][] getPDF(double mean, double xMax) {
        int numBins = 1000; // Adjust the number of bins as needed
        int k = level; // Set the shape parameter (number of events), you can adjust this as needed
        double rateParameter = 1.0 / mean; // Calculate the rate parameter (mean time between events)

        double[][] pdf = new double[2][numBins];
        double binWidth = xMax / numBins;

        for (int i = 0; i < numBins; i++) {
            double x = i * binWidth;
            pdf[0][i] = x;
            pdf[1][i] = (1/slice)*(Math.pow(rateParameter, k) * Math.pow(x, k - 1) * Math.exp(-rateParameter * x)) / factorial(k - 1); //* binWidth;
        }

        return pdf;
    }

    // Helper method to calculate the factorial of a number
    private static double factorial(int n) {
        double result = 1.0;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private static double exponentialDistribution(double mean) {
        Random random = new Random();
        return mean * (-Math.log(1 - random.nextDouble()));
    }
}