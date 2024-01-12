package distributions;

import java.util.Random;

public class ErlangDistribution extends Distribution {

    private int level = 2;

    public void setLevel(int k) {
        this.level=k;
    }


    public ErlangDistribution(DistributionType type) {
        super(type);
    }

    public double getSample(double mean) {
        Random random = new Random();
        double sample = 0;
        for (int i = 0; i < this.level; i++) {
            sample += exponentialDistribution(mean);
        }
        return sample / this.level;
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


    public double[][] getPDF(double mean, double xMax) {
        int numPoints = 1000; // Adjust the number of bins as needed
        int k = level; // Set the shape parameter (number of events), you can adjust this as needed

        double[][] pdf = new double[2][numPoints];
        double stepWidth = xMax / numPoints;

        for (int i = 0; i < numPoints; i++) {
            double x = i * stepWidth;
            pdf[0][i] = x;
            pdf[1][i] = erlangDistributionPDF(x,mean,k); //* stepWidth;
        }
        return pdf;
    }

    private double erlangDistributionPDF(double x, double mean, int k) {
        double rate = k / mean; // Calculate the rate parameter (mean time between events)
        return (Math.pow(rate, k) * Math.pow(x, k - 1) * Math.exp(-rate * x)) / factorial(k - 1);
    }

    // Helper method to calculate the factorial of a number

    private static double exponentialDistribution(double mean) {
        Random random = new Random();
        return mean * (-Math.log(1 - random.nextDouble()));
    }
}