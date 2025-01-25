package distributions;

import results.Histogram;

import java.util.Arrays;

public class ErlangTest {
    public static void main(String[] args) {
        int numSamples = 25000;
        int numBins = 25;
        double mean = 10.0;
        int k = 5;
        double maxX = 0.0;

        ErlangDistribution erlangDistribution = new ErlangDistribution(DistributionType.ERLANG);
        erlangDistribution.setLevel(k);
        double[] samples = erlangDistribution.getSamples(mean, numSamples);
        for (double sample : samples) {
            if (sample > maxX) {
                maxX = sample;
            }
        }
        double[][] pdf = erlangDistribution.getPDF(mean, maxX);

        Histogram.generateHistogram(numBins, samples, pdf, DistributionType.ERLANG.name() + "-" + k);
    }
}

