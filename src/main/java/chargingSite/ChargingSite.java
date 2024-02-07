package chargingSite;


import eventSimulation.EventSimulation;
import exceptions.SitePowerExceededException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import results.Histogram;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class ChargingSite {
    private ArrayList<ChargingPoint> chargingPoints = new ArrayList<>();
    private int numberOfChargingPoints;
    private ArrayList<Double> chargingPowers = new ArrayList<>();
    private double maxSitePower;
    private XYSeries sitePowerSeries = new XYSeries("Site Power");
    private JFreeChart sitePowerChart;
    private ChartPanel chartPanel;
    private JFrame chartFrame;
    private boolean isChartInitialized = false;
    private ArrayList<Color> seriesColors = new ArrayList<>();
    private int seriesCount = 0;



    public ChargingSite(int numberOfChargingPoints, double maxSitePower) {
        this.numberOfChargingPoints = numberOfChargingPoints;
        this.maxSitePower = maxSitePower;
        initializeChargingPoints();
    }

    private void initializeChargingPoints() {
        for (int i = 0; i < numberOfChargingPoints; i++) {
            chargingPowers.add((double) Simulation.MAX_POINT_POWER);
            chargingPoints.add(new ChargingPoint(Simulation.MAX_POINT_POWER));
        }
    }

    public ChargingPoint getChargingPoint(int index) {
        return this.chargingPoints.get(index);
    }

    public ChargingPoint getIdleChargingPoint(PlugType plugType) {
        for (ChargingPoint next : chargingPoints) {
            if (next.getChargedCar() == null) return next; // && next.getPlugTypes().contains(plugType)
        }
        return null;
    }

    public double getMaxSitePower() {
        return maxSitePower;
    }

    public void checkPower() {
        getSitePower();
    }

    List<double[]> dataPoints = new ArrayList<double[]>();

    public double getSitePower() {
        double sitePower = 0;
        int i = 0;

        for (ChargingPoint next : chargingPoints) {
            sitePower += next.getPower();
            i++;
        }
        if (i > numberOfChargingPoints) {
            throw new IllegalStateException("<ChargingSite>.getSitePower summed over " + i + " chargingPoints > " + this.numberOfChargingPoints + " configured!");
        }
        if (sitePower > maxSitePower) {
            sitePower = scaleChargingPower(maxSitePower / sitePower);
            if (sitePower - maxSitePower > 0.0001) {
                throw new SitePowerExceededException("Site power " + sitePower + " is greater than the set maximum " + maxSitePower + " !");
            }
        }
      sitePowerSeries.add(EventSimulation.getCurrentTime(),sitePower);
        return sitePower;
    }

    public double scaleChargingPower(double scale) {
        double newSitePower = 0;

        for (ChargingPoint next : chargingPoints) {
            ElectricVehicle car = next.getChargedCar();
            if (car != null) newSitePower += car.scaleChargingPower(scale);
        }
        return newSitePower;
    }


    public void addSitePowerHistogram(ChartPanel chartPanel) {
        // add JFreeChart sitePowerHistogram to chartPanel;

        JFreeChart chart = Histogram.makeHistogram(sitePowerSeries.toArray()[1], 15);

        sitePowerSeries.clear();
        return;
    }

    private CombinedDomainXYPlot mainPlot;
    private int histogramCount = 0;

    public XYSeries getSitePowerSeries() {
        return sitePowerSeries;
    }

    private void initializeChart() {
        mainPlot = new CombinedDomainXYPlot(new NumberAxis("Values"));
        mainPlot.setGap(10.0);

        sitePowerChart = new JFreeChart("Site Power Distribution Histograms",
                JFreeChart.DEFAULT_TITLE_FONT, mainPlot, true);

        chartPanel = new ChartPanel(sitePowerChart);
        chartFrame = new JFrame("Histograms");
        chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chartFrame.setSize(800, 600);
        chartFrame.setContentPane(chartPanel);
        chartFrame.setVisible(true);

        isChartInitialized = true;
    }


    private double[] convertXYSeriesToDoubleArray(XYSeries series) {
        double[] data = new double[series.getItemCount()];
        for (int i = 0; i < series.getItemCount(); i++) {
            data[i] = series.getY(i).doubleValue();
        }
        return data;
    }

    private HistogramDataset histogramDataset = null;

    public void addSitePower3DHistogram(XYSeries series, double arrivalRate) {
        double[] samples = convertXYSeriesToDoubleArray(series);

        double[] filteredSamples = DoubleStream.of(samples)
                .filter(value -> value != 0)
                .toArray();

        if (filteredSamples.length > 0) {
            if (histogramDataset == null) {
                histogramDataset = new HistogramDataset();
                histogramDataset.setType(HistogramType.SCALE_AREA_TO_1);
            }

            String seriesTitle = "Series " + (arrivalRate);
            histogramDataset.addSeries(seriesTitle, filteredSamples, 15);

            if (!isChartInitialized) {
                initializeChart();
            }

            if (isChartInitialized) {
                JFreeChart histogramChart = ChartFactory.createHistogram(
                        "Histogram", "Value", "Probability",
                        histogramDataset, PlotOrientation.VERTICAL, true, true, false);

                XYPlot plot = (XYPlot) histogramChart.getPlot();
                XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();

                int[] seriesOrder = getSeriesOrder(histogramDataset);

                int backgroundSeries = seriesOrder[seriesOrder.length - 1];
                renderer.setSeriesPaint(backgroundSeries, Color.WHITE);
                renderer.setSeriesOutlinePaint(backgroundSeries, Color.BLACK);

                for (int i = 0; i < seriesOrder.length - 1; i++) {
                    int seriesIndex = seriesOrder[i];
                    Color color = generateTransparentColor();
                    renderer.setSeriesPaint(seriesIndex, color);
                    renderer.setSeriesOutlinePaint(seriesIndex, Color.BLACK);
                }

                if (mainPlot.getSubplots().size() == 0) {
                    mainPlot.add(plot);
                } else {
                    mainPlot.setDataset(histogramDataset);
                    mainPlot.setRenderer(renderer);
                }

                chartFrame.validate();
                chartFrame.repaint();
                saveDataToCSV(chartFrame, seriesTitle, histogramDataset, arrivalRate);
            }
            //saveDataToCSV(chartFrame, seriesTitle, histogramDataset, arrivalRate);
        } else {
            System.out.println("No non-zero data available for histogram.");
        }
    }

    private int[] getSeriesOrder(HistogramDataset dataset) {
        int[] seriesOrder = new int[dataset.getSeriesCount()];
        double[] sums = new double[dataset.getSeriesCount()];

        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            double sum = 0.0;

            for (int j = 0; j < dataset.getItemCount(i); j++) {
                sum += dataset.getYValue(i, j);
            }

            sums[i] = sum;
            seriesOrder[i] = i;
        }

        IntStream.range(0, seriesOrder.length)
                .boxed()
                .sorted(Comparator.comparingDouble(i -> sums[(int) i]).reversed())
                .mapToInt(Integer::intValue)
                .toArray();

        return seriesOrder;
    }
    private Color generateTransparentColor() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        int alpha = 100;
        return new Color(r, g, b, alpha);
    }



    private void initializeSitePowerGraph() {
        if (!isChartInitialized) {
            XYSeriesCollection dataset = new XYSeriesCollection(sitePowerSeries);
            sitePowerChart = ChartFactory.createXYLineChart(
                    "Site Power vs Time",
                    "Time",
                    "Site Power",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false);

            chartPanel = new ChartPanel(sitePowerChart);
            chartFrame = new JFrame();
            chartFrame.setContentPane(chartPanel);
            chartFrame.setTitle("Site Power Graph");
            chartFrame.setSize(600, 400);
            chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            chartFrame.setVisible(true);

            isChartInitialized = true;
        }
    }

    public void addSitePowerGraph() {
        initializeSitePowerGraph();

        Color color = generateUniqueColor(seriesCount);

        XYSeries newSeries = new XYSeries("Series " + seriesCount);
        for (int i = 0; i < sitePowerSeries.getItemCount(); i++) {
            newSeries.add(sitePowerSeries.getX(i), sitePowerSeries.getY(i));
        }
        sitePowerSeries.clear();

        XYSeriesCollection dataset = (XYSeriesCollection) sitePowerChart.getXYPlot().getDataset();
        dataset.addSeries(newSeries);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) sitePowerChart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(seriesCount, color);

        seriesCount++;

        sitePowerChart.getXYPlot().setRenderer(renderer);
        sitePowerChart.fireChartChanged();
        sitePowerSeries.clear();

    }
    private Color generateUniqueColor(int seriesIndex) {
        Random rand = new Random(seriesIndex);
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }
    private String lastFilePath;


    private void saveDataToCSV(JFrame frame, String seriesTitle, HistogramDataset dataset, double arrivalRate) {
        if (lastFilePath == null || !new File(lastFilePath).exists()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");

            int userSelection = fileChooser.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                lastFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            } else {
                return;
            }
        }

        try (FileWriter writer = new FileWriter(lastFilePath, true)) {
            int seriesIndex = dataset.indexOf(seriesTitle);
            if (seriesIndex >= 0) {
                int itemCount = dataset.getItemCount(seriesIndex);
                for (int i = 0; i < itemCount; i++) {
                    double binStart = dataset.getStartXValue(seriesIndex, i);
                    double binEnd = dataset.getEndXValue(seriesIndex, i);
                    double binMidpoint = (binStart + binEnd) / 2.0;
                    double binProbability = dataset.getYValue(seriesIndex, i);

                    writer.append(String.valueOf(binMidpoint)); // SitePower
                    writer.append(';');
                    writer.append(String.valueOf(binProbability)); // Probability
                    writer.append(';');
                    writer.append(String.valueOf(arrivalRate)); // Arrival Rate
                    writer.append("00\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // frame.dispose();
    }


    private double getProbability(double value) {
        double minValue = 1;
        double maxValue = 10;

        double range = maxValue - minValue;

        if (value >= minValue && value <= maxValue) {
            return 1.0 / range;
        } else {
            return 0.0;
        }
    }
}

