package results;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.util.ArrayList;
import java.util.List;
public class Times {
    private String xAxis;
    private String yAxis;
    private List<Double> steps = new ArrayList<>();

    private List<Double> means = new ArrayList<>();
    private List<Double> stds = new ArrayList<>();
    private List<Double> confidences = new ArrayList<>();

    private Statistics functions = new Statistics();

    public Times(String xAxis, String yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public String getxAxis() {
        return xAxis;
    }

    public String getyAxis() {
        return yAxis;
    }

    public List<Double> getSteps() {
        return steps;
    }

    public List<Double> getMeans() {
        return means;
    }

    public List<Double> getStds() {
        return stds;
    }

    public List<Double> getConfidences() {
        return confidences;
    }

    public void addStep(double value) {
        steps.add(value);
    }

    public void addMean(List<Double> values) {
        means.add(functions.getMean(values));
    }

    public void addStds(List<Double> values) {
        stds.add(functions.getStd(values));
    }

    public void addConfidence(List<Double> values, int level) {
        confidences.add(functions.getConfidenceInterval(values, level));
    }

    public void addGraphs(XYSeriesCollection dataset){
        XYSeries meanSeries = new XYSeries("Mean");
        XYSeries stdSeries = new XYSeries("Std");
        XYSeries[] confBars = new XYSeries[steps.size()];
        for (int i = 0; i < steps.size(); i++) {
            double step = steps.get(i);
            double mean = means.get(i);
            double std = stds.get(i);
            double conf = confidences.get(i);
            confBars[i] = new XYSeries("confBar"+i);

            meanSeries.add(step, mean);
            stdSeries.add(step, std);
            confBars[i].add(step, mean - conf);
            confBars[i].add(step, mean + conf);
            dataset.addSeries(confBars[i]);
        }
        dataset.addSeries(meanSeries);
        dataset.addSeries(stdSeries);
    }
}