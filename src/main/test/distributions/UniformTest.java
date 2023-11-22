package distributions;

import results.Histogram;

public class UniformTest {
    public static void main(String[] args) {
        int numSamples = 25000;
        int numBins = 25;
        double mean = 0.5;

        UniformDistribution uniformDistribution = new UniformDistribution(DistributionType.UNIFORM);
        double[] samples = uniformDistribution.getSamples(mean, numSamples);
        double[][] pdf = uniformDistribution.getPDF(mean, 2*mean);

        Histogram.generateHistogram(numBins, samples, pdf, DistributionType.UNIFORM.name());
    }
}
