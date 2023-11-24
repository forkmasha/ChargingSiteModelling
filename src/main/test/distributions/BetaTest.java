package distributions;

import results.Histogram;

public class BetaTest {
    public static void main(String[] args) {
        int numSamples = 250000;
        int numBins = 25;
        double mean = 0.8;

        BetaDistribution betaDistribution = new BetaDistribution(DistributionType.BETA);
        double[] samples = betaDistribution.getSamples(mean, numSamples);
        samples[0] = 0.00001;
        samples[numSamples - 1] = 0.99999;
        double[][] pdf = betaDistribution.getPDF(mean, 1);

        Histogram.generateHistogram(numBins, samples, pdf,DistributionType.BETA.name());

        samples = betaDistribution.getSamples(1 - mean, numSamples);
        samples[0] = 0.00001;
        samples[numSamples - 1] = 0.99999;
        double[][] pdf2 = betaDistribution.getPDF(1-mean, 1);

        Histogram.generateHistogram(numBins, samples, pdf2,DistributionType.BETA.name());

    }
}