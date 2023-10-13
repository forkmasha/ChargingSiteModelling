package distributions;

import results.Histogram;

public class BetaTest {
        public static void main(String[] args) {
            int numSamples = 25000;
            int numBins = 100;
            double mean=0.7;

            BetaDistribution betaDistribution = new BetaDistribution(DistributionType.BETA);
            double[] samples = betaDistribution.getSamples(mean, numSamples);
            samples[0]=0;
            samples[numSamples-1]=1;
            double[][] pdf = betaDistribution.getPDF(mean, 1);

            Histogram.generateHistogram(numBins, samples, pdf);
        }
}
