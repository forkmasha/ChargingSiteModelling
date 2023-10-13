package distributions;

import results.Histogram;

public class GeometricTest {
    public static void main(String[] args) {
        int numSamples = 2500;
        int numBins = 10;
        double mean=0.5;

       GeometricDistribution geometricDistribution = new GeometricDistribution(DistributionType.GEOMETRIC);
        double[] samples = geometricDistribution.getSamples(mean, numSamples);
        double[][] pdf = geometricDistribution.getPDF(mean, 1);

        Histogram.generateHistogram(numBins, samples, pdf);
    }
}

