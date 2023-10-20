package distributions;

import java.util.Random;

public class UniformDistribution extends Distribution {

    public UniformDistribution(DistributionType type) {
        super(type);
    }

    public double getSample(double mean) {
        //Random random = new Random();
        //return 2 * mean * (random.nextDouble());
        return createSample(mean);
    }
    public static double createSample(double mean) {
        Random random = new Random();
        return 2 * mean * (random.nextDouble());
    }
    @Override
    public double[] getSamples(double mean, int count) {
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = createSample(mean);
        }
        return samples;
    }
    public static double[][] getPDF(double mean, double xMax) {
        int numBins = 100; // Adjust the number of bins as needed
        double binWidth = xMax / numBins;
        double constantPDF = 1.0 / xMax; // Calculate the constant PDF value

        double[][] pdf = new double[2][numBins];

        for (int i = 0; i < numBins; i++) {
            pdf[0][i]=i/numBins;
            pdf[1][i] = constantPDF;
        }

        return pdf;
    }
}