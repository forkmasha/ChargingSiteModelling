package results;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.jfree.chart.ChartFactory.createXYLineChart;

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

    public void drawGraph() {
        String title = yAxis + " vs " + xAxis;
        XYSeriesCollection dataset = new XYSeriesCollection();
        //XYSeries testSeries = new XYSeries("Test");
        XYSeries meanSeries = new XYSeries("Mean");
        XYSeries stdSeries = new XYSeries("Std");
        XYSeries upperSeries = new XYSeries("Mean+");
        XYSeries lowerSeries = new XYSeries("Mean-");

        for (int i = 0; i < steps.size(); i++) {
            double step = steps.get(i);
            double mean = means.get(i);
            double std = stds.get(i);
            double conf = confidences.get(i);

            //testSeries.add(i,i/10.0);
            meanSeries.add(step, mean);
            stdSeries.add(step, std);
            lowerSeries.add(step, mean - conf);
            upperSeries.add(step, mean + conf);
        }
        //dataset.addSeries(testSeries);
        dataset.addSeries(meanSeries);
        dataset.addSeries(upperSeries);
        dataset.addSeries(lowerSeries);
        dataset.addSeries(stdSeries);

        JFreeChart chart = createXYLineChart(
                yAxis + " vs " + xAxis,
                xAxis,
                yAxis,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        XYPlot plot = chart.getXYPlot();
        NumberAxis x_Axis = (NumberAxis) plot.getDomainAxis();
        //x_Axis.setTickUnit(new NumberTickUnit(5));
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);

        JFrame frame = new JFrame(yAxis + " vs " + xAxis);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
        chartPanel.repaint();
    }
}