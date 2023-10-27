package distributions;

import results.Histogram;

public class ExponentialTest {
    public static void main(String[] args) {
        int numSamples = 25000;
        int numBins = 25;
        double mean=0.5;
        double maxX=1;

        ExponentialDistribution exponentialDistribution = new ExponentialDistribution(DistributionType.EXPONENTIAL);
        double[] samples = exponentialDistribution.getSamples(mean, numSamples);
        for (double sample : samples) {
            if (sample > maxX) {
                maxX = sample;
            }
        }
        double[][] pdf = exponentialDistribution.getPDF(mean, maxX);


        Histogram.generateHistogram(numBins, samples, pdf,DistributionType.EXPONENTIAL.name());
    }
}
