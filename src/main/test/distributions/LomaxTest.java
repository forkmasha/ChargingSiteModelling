package distributions;

import results.Histogram;

public class LomaxTest {
    public static void main(String[] args) {
        int numSamples = 25000;
        int numBins = 25;
        double mean = 10.0;
        double scale = 0.25;
        double shape = 2.5;
        double maxX = 20.0;

        if (scale<=0||shape<=0){
            System.out.println("ERROR distribution parameters out of range");
        }
        LomaxDistribution lomaxDistribution = new LomaxDistribution(DistributionType.LOMAX);
        //double[] samples = lomaxDistribution.getSamples(mean, numSamples, maxX); // does not yield an exact Lomax distribution!
        double[] samples = lomaxDistribution.getSamples(mean, numSamples);
        int i=0;
        for (double sample : samples) {
            if (sample > maxX) {
                //maxX = sample;
                samples[i] = maxX;
            }
            i++;
        }

        double[][] pdf = lomaxDistribution.getPDF(mean, maxX);
        Histogram.generateHistogram(maxX, numBins, samples, pdf, DistributionType.LOMAX.name());
    }
}