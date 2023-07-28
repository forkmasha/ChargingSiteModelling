package results;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

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


    public void addGraphs(XYSeriesCollection dataset){
        XYSeries meanSeries = new XYSeries("Mean");
        XYSeries stdSeries = new XYSeries("Std");
        //XYSeries upperSeries = new XYSeries("Mean+");
        // XYSeries lowerSeries = new XYSeries("Mean-");
        XYSeries[] confBars = new XYSeries[steps.size()];
        ;
        for (int i = 0; i < steps.size(); i++) {
            double step = steps.get(i);
            double mean = means.get(i);
            double std = stds.get(i);
            double conf = confidences.get(i);
            confBars[i] = new XYSeries("confBar"+i);

            meanSeries.add(step, mean);
            stdSeries.add(step, std);
            //  lowerSeries.add(step, mean - conf);
            //   upperSeries.add(step, mean + conf);
            confBars[i].add(step, mean - conf);
            confBars[i].add(step, mean + conf);
            dataset.addSeries(confBars[i]);
        }

        dataset.addSeries(meanSeries);
        // dataset.addSeries(upperSeries);
        // dataset.addSeries(lowerSeries);
        dataset.addSeries(stdSeries);
    }

      private void drawGraph() {                                  // Working version with confidence Interval
        String title = yAxis + " vs " + xAxis;
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries meanSeries = new XYSeries("Mean");
        XYSeries stdSeries = new XYSeries("Std");
        //XYSeries upperSeries = new XYSeries("Mean+");
       // XYSeries lowerSeries = new XYSeries("Mean-");
          XYSeries[] confBars = new XYSeries[steps.size()];
                  ;
          for (int i = 0; i < steps.size(); i++) {
            double step = steps.get(i);
            double mean = means.get(i);
            double std = stds.get(i);
            double conf = confidences.get(i);
            confBars[i] = new XYSeries("confBar"+i);

            meanSeries.add(step, mean);
            stdSeries.add(step, std);
          //  lowerSeries.add(step, mean - conf);
         //   upperSeries.add(step, mean + conf);
            confBars[i].add(step, mean - conf);
            confBars[i].add(step, mean + conf);
            dataset.addSeries(confBars[i]);
          }

        dataset.addSeries(meanSeries);
            // dataset.addSeries(upperSeries);
          // dataset.addSeries(lowerSeries);
        dataset.addSeries(stdSeries);

        JFreeChart chart = createXYLineChart(
                yAxis + " vs " + xAxis,
                xAxis,
                yAxis,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        XYPlot plot = chart.getXYPlot();
        NumberAxis x_Axis = (NumberAxis) plot.getDomainAxis();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        for(int i=0;i<confBars.length;i++){
           renderer.setSeriesPaint(i,Color.BLUE);
          // renderer.setSeriesShape();
           //renderer.setSeriesShape(i, ShapeUtilities.createDiagonalCross(3,1));
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f,1.5f));
        }
        plot.setRenderer(renderer);

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


    /* public void drawGraph() {                            // YesterdayLastWorkingVersiom
        String title = yAxis + " vs " + xAxis;
        XYSeriesCollection dataset = new XYSeriesCollection();;
        XYSeries meanSeries = new XYSeries("Mean");
        XYSeries stdSeries = new XYSeries("Std");
        XYSeries upperSeries = new XYSeries("Mean+");
        XYSeries lowerSeries = new XYSeries("Mean-");

        for (int i = 0; i < steps.size(); i++) {
            double step = steps.get(i);
            double mean = means.get(i);
            double std = stds.get(i);
            double conf = confidences.get(i);

            meanSeries.add(step, mean);
            stdSeries.add(step, std);
            lowerSeries.add(step, mean - conf);
            upperSeries.add(step, mean + conf);
        }
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
    }*/

    //3 Graphs in One window
   /* public ChartPanel drawGraph(String chartTitle, String yAxisTitle, String xAxisTitle) {//Graph 3 windowsTogether
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries meanSeries = new XYSeries("Mean");
        XYSeries stdSeries = new XYSeries("Std");

        for (int i = 0; i < steps.size(); i++) {
            double step = steps.get(i);
            double mean = means.get(i);
            double std = stds.get(i);
            double conf = confidences.get(i);

            meanSeries.add(step, mean);
            stdSeries.add(step, std);
        }

        dataset.addSeries(meanSeries);
        dataset.addSeries(stdSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle,
                xAxisTitle,
                yAxisTitle,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        NumberAxis x_Axis = (NumberAxis) plot.getDomainAxis();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Add error bars to the renderer
        XYErrorRenderer errorRenderer = new XYErrorRenderer();
        errorRenderer.setBaseLinesVisible(true);
        errorRenderer.setBaseShapesVisible(false);
        plot.setRenderer(errorRenderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 800));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);

        return chartPanel;
    } */

}