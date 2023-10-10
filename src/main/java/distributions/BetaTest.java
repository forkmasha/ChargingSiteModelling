package distributions;

import results.Histogram;

public class BetaTest {
        public static void main(String[] args) {
            int numSamples = 2500;
            int numBins = 10;
            double mean=0.5;

            BetaDistribution betaDistribution = new BetaDistribution(DistributionType.BETA);
            double[] samples = betaDistribution.getSamples(mean, numSamples);
            double[][] pdf = betaDistribution.getPDF(mean, 1);

            Histogram.generateHistogram(numBins, samples, pdf);
        }
}
