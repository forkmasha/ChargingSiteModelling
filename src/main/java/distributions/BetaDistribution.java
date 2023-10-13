package distributions;

import java.util.Random;

public class BetaDistribution extends Distribution {
    private Random random;
    private double alpha = 5;
    private double beta = 2;

    public BetaDistribution(DistributionType type) {
        super(type);
        random = new Random();
        this.alpha = 5;
        this.beta = 2;
    }

    public double getSample(double mean) {
        double[] ab = getShapeParameters(mean);
        double sample = betaDistribution(ab);
        while (sample < 0 || sample > 1) {sample = betaDistribution(ab);}
        return sample;
    }

    public static double createSample(double mean) {
        double[] ab = getShapeParameters(mean);
        double sample = betaDistribution(ab);
        while (sample < 0 || sample > 1) {sample = betaDistribution(ab);}
        return sample;
    }

    private static double[] getShapeParameters(double mean) {
        double[] ab = {1,1};
        if(mean<0) {mean *= -1;}
        if(mean>1) {mean = 1/mean;}
        double omega = 0.8;
        double c = 5;
        ab[0] = 1 + c * omega; // alpha
        ab[1] = 1 + c * (1-omega);  // beta
        return ab;
    }
    private static double betaDistribution(double[] ab) {
        double gamma1 = gammaDistribution(ab[0], 1.0);
        double gamma2 = gammaDistribution(ab[1], 1.0);
        return gamma1*gamma2 / (gamma1 + gamma2);
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
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = createSample(mean);
        }
        return samples;
    }

    @Override
    public double[][] getPDF(double mean, double xMax) {
        int numPoints = 1000;
        double[][] pdfValues = new double[2][numPoints];
        double stepSize = xMax / (numPoints - 1);

        for (int i = 0; i < numPoints; i++) {
            double x = i * stepSize;
            double alpha = 5;
            double beta = 2;
            pdfValues[0][i] = x;
            pdfValues[1][i] = betaDistributionPDF(alpha, beta, x);
        }
        return pdfValues;
    }

    private double betaDistributionPDF(double alpha, double beta, double x) {
        double num = Math.pow(x, alpha - 1) * Math.pow(1 - x, beta - 1);
        double den = betaFunction(alpha, beta);
        return num / den;
    }

    private double betaFunction(double alpha, double beta) {
        return gamaFunction(alpha) * gamaFunction(beta) / gamaFunction(alpha + beta);
    }

    private double gamaFunction(double x) {
        if (x == 1.0) {
            return 1.0;
        } else if (x < 1.0) {
            return gamaFunction(x + 1) / x;
        } else {
            return (x - 1) * gamaFunction(x - 1);
        }
    }
}