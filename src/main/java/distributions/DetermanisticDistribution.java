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
        /*for (double sample : samples) {
            sample = mean;
        }*/
        for (int i = 0; i < count; i++) {
            samples[i] = mean;
        }
        return samples;
    }

 //   public static double[][] getPDF(double mean, double xMax) {
        //return new double[0][0];
   // }

    /*public static double[][] getPDF(double mean, double xMax) {
        double[][] pdf = new double[2][2];
        pdf[0][0] = mean; // Початок інтервалу x (детерміністична точка)
        pdf[0][1] = mean; // Кінець інтервалу x (детерміністична точка)
        pdf[1][0] = 0; // Початок ймовірності (зазвичай 0 для детерміністичного розподілу)
        pdf[1][1] = 1; // Кінець ймовірності (зазвичай 1 для детерміністичного розподілу)
        return pdf;
    }*/

    /*public static double[][] getPDF(double mean, double xMax) {
        double[] xValues = {mean};  // Значення x для детерміністичного розподілу
        double[] yValues = {1.0};   // Відповідні ймовірності для кожного значення x

        double[][] pdf = {xValues, yValues};
        return pdf;
    }*/

    public static double[][] getPDF(double mean, double xMax) {
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