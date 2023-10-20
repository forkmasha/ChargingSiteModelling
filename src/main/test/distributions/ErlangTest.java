package distributions;

import results.Histogram;

public class ErlangTest {
    public static void main(String[] args) {
        int numSamples = 25000;
        int numBins = 100;
        double mean=0.5;

        ErlangDistribution erlangDistribution = new ErlangDistribution(DistributionType.ERLANG);
        double[] samples = erlangDistribution.getSamples(mean, numSamples);
        double[][] pdf = erlangDistribution.getPDF(mean, 1);

        Histogram.generateHistogram(numBins, samples, pdf,DistributionType.ERLANG.name());
    }
}

