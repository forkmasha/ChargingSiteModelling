package distributions;

import results.Histogram;

public class UniformTest {
    public static void main(String[] args) {
        int numSamples = 2500;
        int numBins = 10;
        double mean=0.5;

        UniformDistribution uniformDistribution = new UniformDistribution(DistributionType.UNIFORM);
        double[] samples = uniformDistribution.getSamples(mean, numSamples);
        double[][] pdf = uniformDistribution.getPDF(mean, 1);

        Histogram.generateHistogram(numBins, samples, pdf);
    }
}
