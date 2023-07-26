package results;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
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

    public void plotGraph() {
        // Create the dataset for the graph
        XYSeriesCollection dataset = createDataset(steps, means, stds, confidences);

        // Create the chart and plot
        JFreeChart chart = createChart("Mean Times vs. Arrival Rate", xAxis, yAxis, dataset);

        // Display chart using Swing
        displayChart(chart, "Mean Times");
    }

    private XYSeriesCollection createDataset(List<Double> xData, List<Double> yData,
                                             List<Double> stdData, List<Double> confData) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Data");
        for (int i = 0; i < xData.size(); i++) {
            series.add(xData.get(i), yData.get(i));
        }
        dataset.addSeries(series);
        return dataset;
    }

    private JFreeChart createChart(String title, String xAxisLabel, String yAxisLabel,
                                   XYSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, dataset,PlotOrientation.VERTICAL,true,true,false);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(renderer);

        // Set the range axis as NumberAxis to allow interval markers
        NumberAxis rangeAxis = new NumberAxis(yAxisLabel);
        plot.setRangeAxis(rangeAxis);

        // Add interval markers for std and conf intervals
        addIntervalMarker(plot, stds, Color.LIGHT_GRAY);
        addIntervalMarker(plot, confidences, Color.LIGHT_GRAY);

        return chart;
    }

    private void addIntervalMarker(XYPlot plot, List<Double> intervals, Paint color) {
        for (int i = 0; i < intervals.size(); i++) {
            double value = intervals.get(i);
            IntervalMarker marker = new IntervalMarker(value - 0.5, value + 0.5, color, new BasicStroke(1.0f), null, null, 0.3f);
            plot.addRangeMarker(marker);
        }
    }

    private void displayChart(JFreeChart chart, String chartTitle) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame(chartTitle);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ChartPanel(chart));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}


    /*public void drawGraph() {
        String title=yAxis+" vs "+xAxis;
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
            meanSeries.add(step,mean);
            stdSeries.add(step, std);
            lowerSeries.add(step, mean-conf);
            upperSeries.add(step, mean+conf);
        }
        //dataset.addSeries(testSeries);
        dataset.addSeries(meanSeries);
        dataset.addSeries(upperSeries);
        dataset.addSeries(lowerSeries);
        dataset.addSeries(stdSeries);

        JFreeChart chart = createXYLineChart(
                yAxis+" vs "+xAxis,
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

        JFrame frame = new JFrame(yAxis+" vs "+xAxis);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);

        chartPanel.repaint();
    }*/

   /* public void createLineChart() {
        // Create a dataset for the chart
        DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries("Mean Service Time", new double[][]{getSteps(), getMeans()});
        dataset.addSeries("Mean Queueing Time", new double[][]{getSteps(), getMeans()});
        dataset.addSeries("Mean System Time", new double[][]{getSteps(), getMeans()});

        // Create the chart using JFreeChart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Mean Times vs. Arrival Rate", // Chart title
                getxAxis(), // X-axis label
                getyAxis(), // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize the appearance of the chart
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, false); // Hide data points for "Mean Service Time" series
        renderer.setSeriesShapesVisible(1, false); // Hide data points for "Mean Queueing Time" series
        renderer.setSeriesShapesVisible(2, false); // Hide data points for "Mean System Time" series
        plot.setRenderer(renderer);

        // Create a JPanel to display the chart
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // Create a JFrame to show the chart
        JFrame frame = new JFrame("Mean Times vs. Arrival Rate");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }*/
