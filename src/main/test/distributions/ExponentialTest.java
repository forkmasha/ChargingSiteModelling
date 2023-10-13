package distributions;

import results.Histogram;

public class ExponentialTest {
    public static void main(String[] args) {
        int numSamples = 2500;
        int numBins = 10;
        double mean=0.5;

        ExponentialDistribution exponentialDistribution = new ExponentialDistribution(DistributionType.EXPONENTIAL);
        double[] samples = exponentialDistribution.getSamples(mean, numSamples);
        double[][] pdf = exponentialDistribution.getPDF(mean, 1);

        Histogram.generateHistogram(numBins, samples, pdf);
    }
}
