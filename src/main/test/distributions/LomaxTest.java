package distributions;

import results.Histogram;

public class LomaxTest {
    public static void main(String[] args) {
        int numSamples = 25000;
        int numBins = 25;
        double mean = 2.0;
        double scale = 4.0;
        double shape = 2.0;
        double maxX = 10.0;

        if (scale<=0||shape<=1){
            System.out.println("ERROR distribution parameters out of range");
        }
        LomaxDistribution lomaxDistribution = new LomaxDistribution(DistributionType.LOMAX);
        double[] samples = lomaxDistribution.getSamples(mean, numSamples, maxX);
        for (double sample : samples) {
            if (sample > maxX) {
                maxX = sample;
            }
        }

        double[][] pdf = lomaxDistribution.getPDF(mean, maxX);
        Histogram.generateHistogram(numBins, samples, pdf, DistributionType.LOMAX.name());
    }
}