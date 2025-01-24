package distributions;

public class DetermanisticDistribution extends Distribution{
    public DetermanisticDistribution(DistributionType type) {
        super(type);
    }

    public double getSample(double mean){
        return createSample(mean);
    }

    public static double createSample(double mean){
        return mean;
    }

    @Override
    public double[] getSamples(double mean, int count) {
        double[] samples = new double[count];
        for (int i = 0; i < count; i++) {
            samples[i] = mean;
        }
        return samples;
    }

    public double[][] getPDF(double mean, double xMax) {
        int numPoints = 1000;
        double[][] pdfValues = new double[2][numPoints];
        double stepSize = xMax / (numPoints - 1);

        for (int i = 0; i < numPoints; i++) {
            double x = i * stepSize;
            pdfValues[0][i] = x;
            if(Math.abs(x-mean)<=stepSize){
                pdfValues[1][i]=100;
            }
            else {
                pdfValues[1][i] = 0;
            }
        }
        return pdfValues;
    }
}