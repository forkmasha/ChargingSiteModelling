package distributions;

import java.util.Random;

public class DiscreteErlangDistribution extends Distribution {

    public int level = 2;
    public double slice = 0.25;// 15 minutes by default

    public DiscreteErlangDistribution(DistributionType type) {
        super(type);
    }

    public void setErlangLevel(int newLevel) {
        level = newLevel;
    }

    public void setSliceLength(double newLength) {
        slice = newLength;
    }

    public double getSample(double mean) {
        //Random random = new Random();
        double sample = 0;
        for (int i = 0; i < level; i++) {
            //sample += exponentialDistribution(mean / slice - 1);
            sample += ExponentialDistribution.createSample(mean / slice);
        }
        sample = slice * (0.5 + Math.floor(sample / level));
        if (sample <= 0) System.out.println("Warning: generated ERLANGD sample is zero...");
        return sample;
    }

    public static double createSample(int level, double mean, double slice) {
        //Random random = new Random();
        double sample = 0;
        for (int i = 0; i < level; i++) {
            sample += exponentialDistribution(mean / slice);
        }
        sample = Math.round(sample / level);
        return slice * sample;
    }

    @Override
    public double[] getSamples(double mean, int count) {
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = getSample(mean);
        }
        return samples;
    }


    public double[][] getPDF(double mean, double xMax) {
        int numBins = 1000; // Adjust the number of bins as needed
        int k = level; // Set the shape parameter (number of events), you can adjust this as needed
        double rateParameter = k / mean; // Calculate the rate parameter (mean time between events)

        double[][] pdf = new double[2][numBins];
        double binWidth = xMax / numBins;

        for (int i = 0; i < numBins; i++) {
            double x = i * binWidth;
            pdf[0][i] = x;
            pdf[1][i] = (Math.pow(rateParameter, k) * Math.pow(x, k - 1) * Math.exp(-rateParameter * x)) / factorial(k - 1); //* binWidth;
        }
        //"discretize" the pdf
        double densitiesSum = 0;
        for (int i = 0; i < numBins; i++) {
            int j = (int) (Math.floor(pdf[0][i] / slice));
            int oldi = i;
            double density = 0;
            while (j - Math.floor(pdf[0][i] / slice) == 0) {
                density += pdf[1][i];
                i++;
                if (i >= numBins) break;
            }
            density /= (i - oldi);
            densitiesSum += density;
            for (int l = oldi; l < i; l++) {
                pdf[1][l] = density;
            }
            System.out.println(" " + density + " ");
            i--;
        }
        for (int i = 0; i < numBins; i++) {
            pdf[0][i] += 0.5 * slice;
        }
        System.out.println("\n " + densitiesSum * slice);
        return pdf;
    }

    private static double exponentialDistribution(double mean) {
        Random random = new Random();
        return mean * (-Math.log(1 - random.nextDouble()));
    }
}