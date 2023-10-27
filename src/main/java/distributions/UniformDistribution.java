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
        int numPoints = 1000; // Adjust the number of bins as needed
        double probability = 1.0 / xMax; // Calculate the constant PDF value
        double stepSize = xMax / numPoints;
        double[][] pdf = new double[2][numPoints];

        for (int i = 0; i < numPoints; i++) {
            pdf[0][i] = i * stepSize;
            pdf[1][i] = probability;
        }
        return pdf;
    }
}