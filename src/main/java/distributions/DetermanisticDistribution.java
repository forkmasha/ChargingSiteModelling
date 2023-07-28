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
        for (double sample : samples) {
            sample = mean;
        }
        return samples;
    }

    @Override
    public double[] getPDF(double mean, double xMax) {
        return new double[0];
    }
}
