package distributions;

import results.Histogram;

public class DiscreteErlangTest {
    public static void main(String[] args) {
        int numSamples = 2500;
        int numBins = 10;
        double mean=0.5;

        DiscreteErlangDistribution discreteErlangDistribution = new DiscreteErlangDistribution(DistributionType.ERLANGD);
        double[] samples = discreteErlangDistribution.getSamples(mean, numSamples);
        double[][] pdf = discreteErlangDistribution.getPDF(mean, 1);

        Histogram.generateHistogram(numBins, samples, pdf);
    }
}
