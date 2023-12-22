package results;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Histogram {
   /* public static void generateHistogram(int bins, double samples[], double pdf[][], String distributionName) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.SCALE_AREA_TO_1);
        // Добавьте сгенерированные данные в набор данных
        dataset.addSeries(distributionName + "  Distribution", samples, bins);
        // Создайте гистограмму
        JFreeChart chart = ChartFactory.createHistogram(distributionName, "Values", "Probability Mass", dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();

         XYSeries pdfSeries = new XYSeries(distributionName + " PDF");
        for (int i = 0; i < pdf[0].length; i++) {
            pdfSeries.add(pdf[0][i], pdf[1][i]);
        }
        XYSeriesCollection pdfDataset = new XYSeriesCollection(pdfSeries);
        plot.setDataset(1, pdfDataset);
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(1, renderer2);
        /*
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setRange(0.0, 1.1);

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(0, 5.0);
        */

    // Set colors and transparency for each series
      /*  chart.getPlot().setForegroundAlpha(0.6f); // Adjust transparency (0.0f - fully transparent, 1.0f - fully opaque)
        chart.getPlot().setBackgroundPaint(ChartColor.WHITE); // Set background color

        chart.getXYPlot().getRenderer().setSeriesPaint(0, new ChartColor(0, 122, 255)); // Exponential - Blue

        // Create a chart panel and display the chart in a frame
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, "Do you really want to close this window?", "Yes", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    frame.dispose();
                }
            }
        });

        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);

    } */

    public static void generateHistogram(int bins, double samples[], double pdf[][], String distributionName) {
        double maxX = 0;
        for (double sample : samples) {
            if (sample > maxX) {
                maxX = sample;
            }
        }
        Histogram.generateHistogram(maxX, bins, samples, pdf, distributionName);
    }

    public static void generateHistogram(double maxX, int bins, double samples[], double pdf[][], String distributionName) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.SCALE_AREA_TO_1);
        dataset.addSeries(distributionName + "  Distribution", samples, bins);
        JFreeChart chart = ChartFactory.createHistogram(distributionName, "Values", "Probability Mass", dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        if (pdf!=null) addPDFToHistogram(chart, pdf);

        // Set colors and transparency for each series
        chart.getPlot().setForegroundAlpha(0.6f); // Adjust transparency (0.0f - fully transparent, 1.0f - fully opaque)
        chart.getPlot().setBackgroundPaint(ChartColor.WHITE); // Set background color

        chart.getXYPlot().getRenderer().setSeriesPaint(0, new ChartColor(0, 122, 255)); // Exponential - Blue

        // Create a chart panel and display the chart in a frame
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, "Do you really want to close this window?", "Yes", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    frame.dispose();
                }
            }
        });

        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);

    }

    public static void addPDFToHistogram(JFreeChart chart, double[][] pdf) {
        XYPlot plot = (XYPlot) chart.getPlot();

        XYSeries pdfSeries = new XYSeries("PDF");
        for (int i = 0; i < pdf[0].length; i++) {
            if (!Double.isInfinite(pdf[1][i])) {
                pdfSeries.add(pdf[0][i], pdf[1][i]);
            }
        }

        XYSplineRenderer renderer = new XYSplineRenderer();
        plot.setDataset(1, new XYSeriesCollection(pdfSeries));
        plot.setRenderer(1, renderer);
    }
}

