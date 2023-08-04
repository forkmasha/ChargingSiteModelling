package distributions;

import java.util.Random;

class ErlangDistribution extends Distribution {

    private int level;
    public ErlangDistribution(DistributionType type) {
        super(type);
        this.level=2; // k=2 is used by default
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getSample(double mean) {
        Random random = new Random();
        double sample = 0;
        for(int i=0; i<level; i++){
            sample += exponentialDistribution(mean);
        }
        return sample/level;
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

    @Override
    public double[] getPDF(double mean, double xMax) {
        int numBins = 100; // Adjust the number of bins as needed
        int k = 2; // Set the shape parameter (number of events), you can adjust this as needed
        double rateParameter = 1.0 / mean; // Calculate the rate parameter (mean time between events)

        double[] pdf = new double[numBins];
        double binWidth = xMax / numBins;

        for (int i = 0; i < numBins; i++) {
            double x = i * binWidth;
            pdf[i] = (Math.pow(rateParameter, k) * Math.pow(x, k - 1) * Math.exp(-rateParameter * x)) / factorial(k - 1) * binWidth;
        }

        return pdf;
    }

    // Helper method to calculate the factorial of a number
    private double factorial(int n) {
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