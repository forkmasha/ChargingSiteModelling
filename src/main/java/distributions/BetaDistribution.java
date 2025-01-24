package distributions;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.special.Gamma;

import java.util.Random;


public class BetaDistribution extends Distribution {
    private static double defaultVar = 7.5;

    public BetaDistribution(DistributionType type) {
        super(type);
    }

    public double getSample(double mean) {
        return getSample(mean, defaultVar);
    }

    public double getSample(double mean, double var) {
        double[] ab = getShapeParameters(mean, var);
        double sample = betaDistribution(ab);
        return sample;
    }

    public static double createSample(double mean) {
        return createSample(mean, defaultVar);
    }

    public static double createSample(double mean, double var) {
        double[] ab = getShapeParameters(mean, var);
        double sample = betaDistribution(ab);
        return sample;
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
        return ab;
    }

    private static double betaDistribution(double[] ab) {
        double gamma1 = new GammaDistribution(ab[0], 1.0).sample();
        double gamma2 = new GammaDistribution(ab[1], 1.0).sample();
        return gamma1 / (gamma1 + gamma2);  // @Masha: this is ok! :-)
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
        return getSamples(mean, defaultVar, count);
    }

    public double[] getSamples(double mean, double var, int count) {
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = createSample(mean, var);
        }
        return samples;
    }

    public double[][] getPDF(double mean, double xMax) {
        int numPoints = 1000;
        double[][] pdfValues = new double[2][numPoints];
        double stepSize = xMax / (numPoints - 1);
        double[] ab = getShapeParameters(mean, defaultVar);
        double alpha = ab[0];
        double beta = ab[1];
        for (int i = 0; i < numPoints; i++) {
            double x = i * stepSize;
            pdfValues[0][i] = x;
            pdfValues[1][i] = betaDistributionPDF(alpha, beta, x); // / (1-Math.abs(0.5-mean));
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
        return Gamma.gamma(alpha) * Gamma.gamma(beta) / Gamma.gamma((alpha + beta));
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