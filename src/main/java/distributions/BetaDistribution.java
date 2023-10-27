package distributions;

import java.util.Random;


public class BetaDistribution extends Distribution {
    private Random random;
    private static double defaultVar = 7.5;
    //private double alpha = 5;
    //private double beta = 2;

    public BetaDistribution(DistributionType type) {
        super(type);
        random = new Random();
        //this.alpha = 5;
        //this.beta = 2;
    }

    public double getSample(double mean) {
        return getSample(mean, defaultVar);
    }

    public double getSample(double mean, double var) {
        double[] ab = getShapeParameters(mean, var);
        double sample = betaDistribution(ab);
        //while (sample < 0 || sample > 1) {sample = betaDistribution(ab);}
        return sample;
    }

    public static double createSample(double mean) {
        return createSample(mean, defaultVar);
    }

    public static double createSample(double mean, double var) {
        double[] ab = getShapeParameters(mean, var);
        double sample = betaDistribution(ab);
        //while (sample < 0 || sample > 1) {sample = betaDistribution(ab);}
        return sample;
    }

    private static double[] getShapeParameters(double mean) {
        return getShapeParameters(mean, defaultVar);
    }

    private static double[] getShapeParameters(double mean, double var) {
        double[] ab = {1, 1};
        if (mean < 0) {
            mean *= -1;
        }
        if (mean > 1) {
            mean = 1 / mean;
        }
        ab[0] = var * mean; // alpha
        ab[1] = var * (1 - mean);  // beta
        //ab[0] = 0.1; ab[1] = 0.1; // use this line to test different shapes (independent of mean)
        return ab;
    }

    private static double betaDistribution(double[] ab) {
        double gamma1 = gammaDistribution(ab[0], 1.0);
        double gamma2 = gammaDistribution(ab[1], 1.0);
        return gamma1 / (gamma1 + gamma2);  // @Masha: this is ok! :-)
        // return (gamma1 * gamma2) / (gamma1 + gamma2); // that's for the Beta-Function NOT the Beta Distribution
        // return gammaDistribution(ab[0] + ab[1], 1.0) / (gamma1 + gamma2); // a variant I found that not works
    }

    public static double gammaDistribution(double shape, double scale) {
        Random random = new Random();
        double shapeFloor = Math.floor(shape);
        double fraction = shape - shapeFloor;
        double result = 0.0;
        for (int i = 0; i < shapeFloor; i++) {
            result += -Math.log(random.nextDouble());
        }
        if (fraction > 0) {
            result += -Math.log(random.nextDouble()) * fraction;
        }
        return result * scale;
    }

    @Override
    public double[] getSamples(double mean, int count) {
        //double[] samples = new double[count];
        //for (int i = 0; i < count; i++) {
        //    samples[i] = createSample(mean,defaultVar);
        //}
        //return samples;
        return getSamples(mean, defaultVar, count);
    }

    public double[] getSamples(double mean, double var, int count) {
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = createSample(mean, var);
        }
        return samples;
    }


    public static double[][] getPDF(double mean, double xMax) {
        int numPoints = 1000;
        double[][] pdfValues = new double[2][numPoints];
        double stepSize = xMax / (numPoints - 1);
        double[] ab = getShapeParameters(mean, defaultVar);
        double alpha = ab[0];
        double beta = ab[1];
        for (int i = 0; i < numPoints; i++) {
            double x = i * stepSize;
            pdfValues[0][i] = x;
            pdfValues[1][i] = betaDistributionPDF(alpha, beta, x);
        }
        return pdfValues;
    }

    private static double betaDistributionPDF(double alpha, double beta, double x) {
        double num = Math.pow(x, alpha - 1) * Math.pow(1 - x, beta - 1);
        double den = betaFunction(alpha, beta);
        System.out.println(" " + num + " " + den);
        return num / den;
    }

    private static double betaFunction(double alpha, double beta) {
        return gamaFunction((int) alpha) * gamaFunction((int) (beta)) / gamaFunction((int)(alpha + beta));
    }

    private static double gamaFunction(int x) {
        if (x == 1.0) {
            return 1.0;
        } else if (x < 1.0) {
            return gamaFunction(x + 1) / x;
        } else {
            return (x - 1) * gamaFunction(x - 1);
        }
    }
}