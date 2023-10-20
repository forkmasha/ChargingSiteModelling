package distributions;

import results.Histogram;

public class GeometricTest {
    public static void main(String[] args) {
        int numSamples = 25000;
        int numBins = 100;
        double mean=50;

       GeometricDistribution geometricDistribution = new GeometricDistribution(DistributionType.GEOMETRIC);
        double[] samples = geometricDistribution.getSamples(mean, numSamples);
        double[][] pdf = geometricDistribution.getPDF(mean, 1);

        Histogram.generateHistogram(numBins, samples, pdf,DistributionType.EXPONENTIAL.name());
    }
}

