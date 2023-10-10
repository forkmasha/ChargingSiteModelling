package distributions;

import java.util.Random;

public class ExponentialDistribution extends Distribution {

    static Random random = new Random();

    public ExponentialDistribution(DistributionType type) {
        super(type);
    }

    @Override
    public double getSample(double mean) {
        //
        return mean * (-Math.log(1 - random.nextDouble()));
    }

    public static double createSample(double mean) {
        Random random = new Random();
        return mean * (-Math.log(1 - random.nextDouble()));
    }

    @Override
    public double[] getSamples(double mean, int count) {
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = getSample(mean);
        }
        return samples;
    }

    @Override
    public double[][] getPDF(double mean, double xMax) {
        // Implement PDF calculation for Exponential distribution
        double lambda = 1.0 / mean;
        int numBins = 100; // Adjust the number of bins as needed
        double[][] pdf = new double[2][numBins];
        double binWidth = xMax / numBins;

        for (int i = 0; i < numBins; i++) {
            double x = i * binWidth;
            pdf[0][i]=x;
            pdf[1][i] = lambda * Math.exp(-lambda * x) * binWidth;
        }
        return pdf;
    }

    private double exponentialDistribution(double mean) {
        Random random = new Random();
        return mean * (-Math.log(1 - random.nextDouble()));
    }
}