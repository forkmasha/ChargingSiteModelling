package distributions;

import results.Histogram;

public class DiscreteErlangTest {
    public static void main(String[] args) {
        int numSamples = 2500000;
        int numBins = 25;
        int level = 2;
        double slice = 0.25;
        double mean = 5;
        double maxX = 1.0;

        DiscreteErlangDistribution discreteErlangDistribution = new DiscreteErlangDistribution(DistributionType.ERLANGD);
        discreteErlangDistribution.setErlangLevel(level);
        discreteErlangDistribution.setSliceLength(slice);
        double[] samples = discreteErlangDistribution.getSamples(mean, numSamples);
        for (double sample : samples) {
            if (sample > maxX) {
                maxX = sample;
            }
        }
       // maxX=Math.ceil(maxX);
        double[][] pdf = discreteErlangDistribution.getPDF(mean, maxX);

        numBins = (int) Math.round(maxX/slice) - 1;
        Histogram.generateHistogram(numBins, samples, pdf, DistributionType.ERLANGD.name());
    }
}
