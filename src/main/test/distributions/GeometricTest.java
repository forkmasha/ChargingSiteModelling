package distributions;

import results.Histogram;

public class GeometricTest {
    public static void main(String[] args) {
        int numSamples = 25000;
        int numBins = 25;
        double mean = 50;
        double maxX = 1.0;

        GeometricDistribution geometricDistribution = new GeometricDistribution(DistributionType.GEOMETRIC);
        double[] samples = geometricDistribution.getSamples(mean, numSamples);
        for (double sample : samples) {
            if (sample > maxX) {
                maxX = sample;
            }
        }
            double[][] pdf = geometricDistribution.getPDF(mean, maxX);

            Histogram.generateHistogram(numBins, samples, pdf, DistributionType.GEOMETRIC.name());
        }
    }

