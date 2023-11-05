package distributions;

import java.util.Random;

public class ErlangDistribution extends Distribution {

    public static final int level = 2;


    public ErlangDistribution(DistributionType type) {
        super(type);
    }

    public double getSample(double mean) {
        Random random = new Random();
        double sample = 0;
        for (int i = 0; i < level; i++) {
            sample += exponentialDistribution(mean);
        }
        return sample / level;
    }

    public static double createSample(double mean) {
        Random random = new Random();
        double sample = exponentialDistribution(mean);
        return sample;
    }

    @Override
    public double[] getSamples(double mean, int count) {
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = getSample(mean);
        }
        return samples;
    }


    public static double[][] getPDF(double mean, double xMax) {
        int numPoints = 1000; // Adjust the number of bins as needed
        int k = level; // Set the shape parameter (number of events), you can adjust this as needed
        double rateParameter = 1.0 / mean; // Calculate the rate parameter (mean time between events)

        double[][] pdf = new double[2][numPoints];
        double stepWidth = xMax / numPoints;

        for (int i = 0; i < numPoints; i++) {
            double x = i * stepWidth;
            pdf[0][i] = x;
            pdf[1][i] = (Math.pow(rateParameter, k) * Math.pow(x, k - 1) * Math.exp(-rateParameter * x)) / factorial(k - 1); //* stepWidth;
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