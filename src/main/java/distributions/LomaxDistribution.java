package distributions;

import java.util.Random;

public class LomaxDistribution extends Distribution {

    private double scale = 4;
    private double shape = 2;
    private Random random = new Random();

    public LomaxDistribution(DistributionType type) {
        super(DistributionType.LOMAX);
        //   this.scale = scale;
        //   this.shape = shape;
    }

    @Override
    public double getSample(double mean) {
        double u = random.nextDouble();
        //mean=scale/(shape-1)
        return mean * (shape - 1) * (Math.pow(1 - u, -1.0 / shape) - 1);
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
        int numPoints = 100;
        double[][] pdf = new double[2][numPoints];

        double step = xMax / numPoints;
        for (int i = 0; i < numPoints; i++) {
            double x = i * step;
            double pdfValue = lomaxDistributionPDF(x, mean, scale, shape);
            pdf[0][i] = x;
            pdf[1][i] = pdfValue;
        }

        return pdf;
    }

    private double lomaxDistributionPDF(double x, double mean, double scale, double shape) {
        double pdfValue = (shape / scale) * Math.pow(1 + (x - mean) / scale, -1 - shape);
        return pdfValue;
    }
}