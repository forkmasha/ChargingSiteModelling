package results;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import javax.swing.*;

public class Histogram {
    public static void generateHistogram(int bins, double samples[],double pdf[][]){
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.SCALE_AREA_TO_1);
        // Add the generated data to the dataset
        dataset.addSeries("Histogram", samples, bins);
        // Create the histogram chart
        JFreeChart chart = ChartFactory.createHistogram("Distribution", "Values", "Probability", dataset, PlotOrientation.VERTICAL, true, true, false);

       /* for(int i=0;i<samples.length;i++){
            samples[i]/=numSamples;
       }*/

        // Set colors and transparency for each series
        chart.getPlot().setForegroundAlpha(0.6f); // Adjust transparency (0.0f - fully transparent, 1.0f - fully opaque)
        chart.getPlot().setBackgroundPaint(ChartColor.WHITE); // Set background color

        chart.getXYPlot().getRenderer().setSeriesPaint(0, new ChartColor(0, 122, 255)); // Exponential - Blue

        // Create a chart panel and display the chart in a frame
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }
}