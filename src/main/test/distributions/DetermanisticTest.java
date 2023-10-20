package distributions;
import results.Histogram;

public class DetermanisticTest {
        public static void main(String[] args) {
            int numSamples = 2500;
            int numBins = 101;
            double mean=0.5;

            DetermanisticDistribution determanisticDistribution = new DetermanisticDistribution(DistributionType.DETERMINISTIC);
            double[] samples = determanisticDistribution.getSamples(mean, numSamples);
            samples[0]=0;
            samples[numSamples-1]=1;
            double[][] pdf = determanisticDistribution.getPDF(mean, 1);

            Histogram.generateHistogram(numBins, samples, pdf,DistributionType.DETERMINISTIC.name());
        }
    }


