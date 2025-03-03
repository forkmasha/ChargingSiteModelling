package results;

import java.util.Collections;
import java.util.List;

public class Statistics {
    public Statistics() {
    }

    public double getMean(List<Double> values) {
        return this.calculateMean(values);
    }
    public double getVariance(List<Double> values) {
        return this.calculateVariance(values);
    }
    public double getStd(List<Double> values) {
        return this.calculateStandardDeviation(values);
    }
    public double getMax(List<Double> values) {
        return this.calculateMax(values);
    }
    public double get90thQuantile (List<Double> values){
        return this.calculate90thQuantile(values);
    }

    public double getMin(List<Double> values) {
        return this.calculateMin(values);
    }

    public double get10thQuantile(List<Double> values) {
        return this.calculate10thQuantile(values);
    }

    public double getConfidenceInterval(List<Double> values, int level) {
        return this.calculateConfidenceInterval(values, level);
    }


    private double calculateMean(List<Double> values) {
        double sum = 0.0;
        for (Double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    private double calculateVariance(List<Double> values) {
        if (values.size() < 2) {
            return Double.MAX_VALUE; // infinite
        }
        double mean = calculateMean(values);
        double sumSquaredDifferences = 0.0;
        for (Double value : values) {
            double difference = value - mean;
            sumSquaredDifferences += difference * difference;
        }
        return sumSquaredDifferences / (values.size() - 1);
    }

    private double calculateStandardDeviation(List<Double> values) {
        if (values.size() < 2) {
            return Double.MAX_VALUE; // infinite
        }
        return Math.sqrt(calculateVariance(values));
    }

    private double calculateMax(List<Double> values) {

        double max = values.get(0);
        for (Double value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private double calculate90thQuantile(List<Double> values) {
        // First, sort the list of values in ascending order
        Collections.sort(values);

        // Calculate the index corresponding to the 90th percentile
        int index = (int) (0.9 * (values.size() - 1));

        // If the index is not an integer, you can interpolate between the two nearest values
        double lowerValue = values.get(index);
        double upperValue = values.get(index + 1);

        // Calculate the interpolated quantile value
        double percentile = lowerValue + (0.9 * (values.size() - 1) - index) * (upperValue - lowerValue);

        return percentile;
    }

    private double calculateMin(List<Double> values) {
        double min = values.get(0);
        for (Double value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }
    private double calculate10thQuantile(List<Double> values) {
        // First, sort the list of values in ascending order
        Collections.sort(values);

        // Calculate the index corresponding to the 10th percentile
        int index = (int) (0.1 * (values.size() - 1));

        // If the index is not an integer, you can interpolate between the two nearest values
        double lowerValue = values.get(index);
        double upperValue = values.get(index + 1);

        // Calculate the interpolated quantile value
        double percentile = lowerValue + (0.1 * (values.size() - 1) - index) * (upperValue - lowerValue);

        return percentile;
    }

    private double calculateConfidenceInterval(List<Double> values, int level) {
        if (values.size() < 2) {
            return Double.MAX_VALUE; // infinite
        }


        // columnNames = {"Degrees of Freedom", "80%", "90%", "95%", "98%", "99%"};
        double[][] zScoreTable = {
                {1, 3.078, 6.314, 12.706, 31.821, 63.65},
                {2, 1.886, 2.920, 4.303, 6.965, 9.925},
                {3, 1.638, 2.353, 3.182, 4.541, 5.841},
                {4, 1.533, 2.132, 2.776, 3.747, 4.604},
                {5, 1.476, 2.015, 2.571, 3.365, 4.032},
                {6, 1.440, 1.943, 2.447, 3.143, 3.707},
                {7, 1.415, 1.895, 2.365, 2.998, 3.499},
                {8, 1.397, 1.860, 2.306, 2.896, 3.355},
                {9, 1.383, 1.833, 2.262, 2.821, 3.250},
                {10, 1.372, 1.812, 2.228, 2.764, 3.169},
                {11, 1.363, 1.796, 2.201, 2.718, 3.106},
                {12, 1.356, 1.782, 2.179, 2.681, 3.055},
                {13, 1.350, 1.771, 2.160, 2.650, 3.012},
                {14, 1.345, 1.761, 2.145, 2.624, 2.977},
                {15, 1.341, 1.753, 2.131, 2.602, 2.947},
                {16, 1.337, 1.746, 2.120, 2.583, 2.921},
                {17, 1.333, 1.740, 2.110, 2.567, 2.898},
                {18, 1.330, 1.734, 2.101, 2.552, 2.878},
                {19, 1.328, 1.729, 2.093, 2.539, 2.861},
                {20, 1.325, 1.725, 2.086, 2.528, 2.845},
                {21, 1.323, 1.721, 2.080, 2.518, 2.831},
                {22, 1.321, 1.717, 2.074, 2.508, 2.819},
                {23, 1.319, 1.714, 2.069, 2.500, 2.807},
                {24, 1.318, 1.711, 2.064, 2.492, 2.797},
                {25, 1.316, 1.708, 2.060, 2.485, 2.787},
                {26, 1.315, 1.706, 2.056, 2.479, 2.779},
                {27, 1.314, 1.703, 2.052, 2.473, 2.771},
                {28, 1.313, 1.701, 2.048, 2.467, 2.763},
                {29, 1.311, 1.699, 2.045, 2.462, 2.756},
                {30, 1.310, 1.697, 2.042, 2.457, 2.750},
                {31, 1.309, 1.696, 2.040, 2.453, 2.744},
                {32, 1.309, 1.694, 2.037, 2.449, 2.738},
                {33, 1.308, 1.692, 2.035, 2.445, 2.733},
                {34, 1.307, 1.691, 2.032, 2.441, 2.728},
                {35, 1.306, 1.690, 2.030, 2.438, 2.724},
                {36, 1.306, 1.688, 2.028, 2.434, 2.719},
                {37, 1.305, 1.687, 2.026, 2.431, 2.715},
                {38, 1.304, 1.686, 2.024, 2.429, 2.712},
                {39, 1.304, 1.685, 2.023, 2.426, 2.708},
                {40, 1.303, 1.684, 2.021, 2.423, 2.704},
                {41, 1.303, 1.683, 2.020, 2.421, 2.701},
                {42, 1.302, 1.682, 2.018, 2.418, 2.698},
                {43, 1.302, 1.681, 2.017, 2.416, 2.695},
                {44, 1.301, 1.680, 2.015, 2.414, 2.692},
                {45, 1.301, 1.679, 2.014, 2.412, 2.690},
                {46, 1.300, 1.679, 2.013, 2.410, 2.687},
                {47, 1.300, 1.678, 2.012, 2.408, 2.685},
                {48, 1.299, 1.677, 2.011, 2.407, 2.682},
                {49, 1.299, 1.677, 2.010, 2.405, 2.680},
                {50, 1.299, 1.676, 2.009, 2.403, 2.678},
                {51, 1.298, 1.675, 2.008, 2.402, 2.676},
                {52, 1.298, 1.675, 2.007, 2.400, 2.674},
                {53, 1.298, 1.674, 2.006, 2.399, 2.672},
                {54, 1.297, 1.674, 2.005, 2.397, 2.670},
                {55, 1.297, 1.673, 2.004, 2.396, 2.668},
                {56, 1.297, 1.673, 2.003, 2.395, 2.667},
                {57, 1.297, 1.672, 2.002, 2.394, 2.665},
                {58, 1.296, 1.672, 2.002, 2.392, 2.663},
                {59, 1.296, 1.671, 2.001, 2.391, 2.662},
                {60, 1.296, 1.671, 2.000, 2.390, 2.660},
                {61, 1.296, 1.670, 2.000, 2.389, 2.659},
                {62, 1.295, 1.670, 1.999, 2.388, 2.657},
                {63, 1.295, 1.669, 1.998, 2.387, 2.656},
                {64, 1.295, 1.669, 1.998, 2.386, 2.655},
                {65, 1.295, 1.669, 1.997, 2.385, 2.654},
                {66, 1.295, 1.668, 1.997, 2.384, 2.652},
                {67, 1.294, 1.668, 1.996, 2.383, 2.651},
                {68, 1.294, 1.668, 1.995, 2.382, 2.650},
                {69, 1.294, 1.667, 1.995, 2.382, 2.649},
                {70, 1.294, 1.667, 1.994, 2.381, 2.648},
                {71, 1.294, 1.667, 1.994, 2.380, 2.647},
                {72, 1.293, 1.666, 1.993, 2.379, 2.646},
                {73, 1.293, 1.666, 1.993, 2.379, 2.645},
                {74, 1.293, 1.666, 1.993, 2.378, 2.644},
                {75, 1.293, 1.665, 1.992, 2.377, 2.643},
                {76, 1.293, 1.665, 1.992, 2.376, 2.642},
                {77, 1.293, 1.665, 1.991, 2.376, 2.641},
                {78, 1.292, 1.665, 1.991, 2.375, 2.640},
                {79, 1.292, 1.664, 1.990, 2.374, 2.640},
                {80, 1.292, 1.664, 1.990, 2.374, 2.639},
                {81, 1.292, 1.664, 1.990, 2.373, 2.638},
                {82, 1.292, 1.664, 1.989, 2.373, 2.637},
                {83, 1.292, 1.663, 1.989, 2.372, 2.636},
                {84, 1.292, 1.663, 1.989, 2.372, 2.636},
                {85, 1.292, 1.663, 1.988, 2.371, 2.635},
                {86, 1.291, 1.663, 1.988, 2.370, 2.634},
                {87, 1.291, 1.663, 1.988, 2.370, 2.634},
                {88, 1.291, 1.662, 1.987, 2.369, 2.633},
                {89, 1.291, 1.662, 1.987, 2.369, 2.632},
                {90, 1.291, 1.662, 1.987, 2.368, 2.632},
                {91, 1.291, 1.662, 1.986, 2.368, 2.631},
                {92, 1.291, 1.662, 1.986, 2.368, 2.630},
                {93, 1.291, 1.661, 1.986, 2.367, 2.630},
                {94, 1.291, 1.661, 1.986, 2.367, 2.629},
                {95, 1.291, 1.661, 1.985, 2.366, 2.629},
                {96, 1.290, 1.661, 1.985, 2.366, 2.628},
                {97, 1.290, 1.661, 1.985, 2.365, 2.627},
                {98, 1.290, 1.661, 1.984, 2.365, 2.627},
                {99, 1.290, 1.660, 1.984, 2.365, 2.626},
                {100, 1.290, 1.660, 1.984, 2.364, 2.626},
                {101, 1.290, 1.660, 1.984, 2.364, 2.625},
                {102, 1.290, 1.660, 1.983, 2.363, 2.625},
                {103, 1.290, 1.660, 1.983, 2.363, 2.624},
                {104, 1.290, 1.660, 1.983, 2.363, 2.624},
                {105, 1.290, 1.659, 1.983, 2.362, 2.623},
                {1000, 1.282, 1.646, 1.962, 2.334, 2.581}, // line for 106 to 1000
                {1001, 1.282, 1.645, 1.960, 2.330, 2.576}, // line for 1000+
        };

        double stdDev = calculateStandardDeviation(values);
        double zScore = 100;
        int levelID = 0;

        switch (level) {
            case 80 -> levelID = 1;
            case 90 -> levelID = 2;
            case 95 -> levelID = 3;
            case 98 -> levelID = 4;
            case 99 -> levelID = 5;
            default -> System.out.println("Error: Confidence level "+levelID+"% is not available in table");
        }

        // Calculate the confidence interval
        int degreeOfFreedom = values.size()-1;
        if (degreeOfFreedom > 1000) {
            zScore = zScoreTable[106][levelID]; // large enough sample size to work with default zScores
        } else if (degreeOfFreedom > 105) {
            zScore = zScoreTable[105][levelID]; // large enough sample size to work with default zScores
        } else {
            zScore = zScoreTable[degreeOfFreedom-1][levelID]; // use the higher zScores from the table
        }
        // other calculation of the zScore [https://stackoverflow.com/questions/21730285/calculating-t-inverse]
        // needs: import org.apache.commons.math3.distribution.TDistribution; but apache.commons not accessible?
        double marginOfError = (zScore * stdDev) / Math.sqrt(values.size());
        return marginOfError;
    }
}