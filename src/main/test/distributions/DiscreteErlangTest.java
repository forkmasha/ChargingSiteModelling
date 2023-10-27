package distributions;

import results.Histogram;

public class DiscreteErlangTest {
    public static void main(String[] args) {
        int numSamples = 2500000;
        int numBins = 25;
        double mean = 2;
        double maxX = 1.0;

        DiscreteErlangDistribution discreteErlangDistribution = new DiscreteErlangDistribution(DistributionType.ERLANGD);
        double[] samples = discreteErlangDistribution.getSamples(mean, numSamples);
        for (double sample : samples) {
            if (sample > maxX) {
                maxX = sample;
            }
        }
       // maxX=Math.ceil(maxX);
        double[][] pdf = discreteErlangDistribution.getPDF(mean, maxX);

        Histogram.generateHistogram(numBins, samples, pdf, DistributionType.ERLANGD.name());
    }
}
