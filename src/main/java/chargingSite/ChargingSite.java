package chargingSite;

import exceptions.SitePowerExceededException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import results.Histogram;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
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
    private ArrayList sitePower1 = new ArrayList<>();

    public ArrayList getSitePower1() {
        return sitePower1;
    }


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
        //sitePowerSeries.add(EventSimulation.getCurrentTime(), sitePower);

        //  sitePower1.add(sitePower);
        addSitePower(sitePower);
        return sitePower;
    }

    private void addSitePower(double power) {
        sitePower1.add(power);
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

            String seriesTitle = "Series " + (++histogramCount);
            histogramDataset.addSeries(seriesTitle, filteredSamples, 15); // Додавання серії до існуючого датасету

            if (!isChartInitialized) {
                initializeChart();
            }

            if (isChartInitialized) {
                JFreeChart histogramChart = ChartFactory.createHistogram(
                        "Histogram", "Value", "Frequency",
                        histogramDataset, PlotOrientation.VERTICAL, true, true, false);

                XYPlot plot = (XYPlot) histogramChart.getPlot();
                XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();

                // Sort series by the sum of their values in descending order
                int[] seriesOrder = getSeriesOrder(histogramDataset);

                // Set background color and outline color for the largest series (in the back)
                int backgroundSeries = seriesOrder[seriesOrder.length - 1];
                renderer.setSeriesPaint(backgroundSeries, Color.WHITE); // Background color
                renderer.setSeriesOutlinePaint(backgroundSeries, Color.BLACK); // Outline color

                // Set colors for the other series (in the front)
                for (int i = 0; i < seriesOrder.length - 1; i++) {
                    int seriesIndex = seriesOrder[i];
                    Color color = generateTransparentColor();
                    renderer.setSeriesPaint(seriesIndex, color);
                    renderer.setSeriesOutlinePaint(seriesIndex, Color.BLACK); // Outline color
                }

                if (mainPlot.getSubplots().size() == 0) {
                    mainPlot.add(plot);
                } else {
                    mainPlot.setDataset(histogramDataset);
                    mainPlot.setRenderer(renderer);
                }

                chartFrame.validate();
                chartFrame.repaint();
            }

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

        // Sort seriesOrder array based on sums in descending order
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
   /* public static void plotHistogram(ArrayList<Double> data, int numBins) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        double min = Collections.min(data);
        double max = Collections.max(data);
        double binWidth = (max - min) / numBins;

        // Заповнення гістограми
        int[] bins = new int[numBins];
        for (double value : data) {
            int binIndex = (int) ((value - min) / binWidth);
            if (binIndex == numBins) {
                binIndex--;
            }
            bins[binIndex]++;
        }

        for (int i = 0; i < numBins; i++) {
            dataset.addValue(bins[i], "Frequency", String.format("%.2f - %.2f", min + i * binWidth, min + (i + 1) * binWidth));
        }

        // Створення графіку
        JFreeChart chart = ChartFactory.createBarChart("Histogram", "Site Power", "Frequency", dataset,PlotOrientation.VERTICAL,false,false,false);

        // Відображення графіку у вікні
        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }*/

      /*public static void plotHistogram(ArrayList<Double> data, int numBins) {
        // Створення гістограми
        HashMap<Integer, Integer> histogram = new HashMap<>();
        double min = Collections.min(data);
        double max = Collections.max(data);
        double binWidth = (max - min) / numBins;

        // Заповнення гістограми
        for (double value : data) {
            int binIndex = (int) ((value - min) / binWidth);
            histogram.put(binIndex, histogram.getOrDefault(binIndex, 0) + 1);
        }

        // Вивід гістограми
        for (int binIndex = 0; binIndex < numBins; binIndex++) {
            int count = histogram.getOrDefault(binIndex, 0);
            double binStart = min + binIndex * binWidth;
            double binEnd = binStart + binWidth;
            System.out.printf("[%f - %f]: %d\n", binStart, binEnd, count);
        }
    }*/

     /*public static void plotHistogram(ArrayList<Double> data, int numBins) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        double min = Collections.min(data);
        double max = Collections.max(data);
        double binWidth = (max - min) / numBins;

        // Заповнення гістограми
        int[] bins = new int[numBins];
        for (double value : data) {
            int binIndex = (int) ((value - min) / binWidth);
            if (binIndex == numBins) {
                binIndex--;
            }
            bins[binIndex]++;
        }

        for (int i = 0; i < numBins; i++) {
            dataset.addValue(bins[i], "Frequency", String.format("%.2f - %.2f", min + i * binWidth, min + (i + 1) * binWidth));
        }

        // Створення графіку
        JFreeChart chart = ChartFactory.createBarChart3D("Histogram", "Site Power", "Frequency", dataset, PlotOrientation.VERTICAL, false, false, false);

        // Відображення графіку у вікні
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }*/

  /*  private static DefaultCategoryDataset dataset;
    private static JFreeChart chart;
    private static JFrame frame;

    public static void plotHistogram(ArrayList<Double> data, int numBins) {
        if (dataset == null) {
            dataset = new DefaultCategoryDataset();
            chart = ChartFactory.createBarChart3D("Histogram", "Site Power", "Frequency", dataset, PlotOrientation.VERTICAL, false, false, false);
            frame = new JFrame("Histogram");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ChartPanel chartPanel = new ChartPanel(chart);
            frame.getContentPane().add(chartPanel);
            frame.pack();
            frame.setVisible(true);
        }

        double min = Collections.min(data);
        double max = Collections.max(data);
        double binWidth = (max - min) / numBins;

        int[] bins = new int[numBins];
        for (double value : data) {
            int binIndex = (int) ((value - min) / binWidth);
            if (binIndex == numBins) {
                binIndex--;
            }
            bins[binIndex]++;
        }

        Color color = getRandomColor();

        for (int i = 0; i < numBins; i++) {
            dataset.addValue(bins[i], "Frequency", String.format("%.2f - %.2f", min + i * binWidth, min + (i + 1) * binWidth));
            chart.getCategoryPlot().getRenderer().setSeriesPaint(i, color);
        }
    }

    private static Color getRandomColor() {
        return new Color((int) (Math.random() * 0x1000000));
    }*/


 /*   private static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private static int seriesCounter = 0;

    public static void plotHistogram(ArrayList<Double> data, int numBins) {
        double min = Collections.min(data);
        double max = Collections.max(data);
        double binWidth = (max - min) / numBins;

        // Заповнення гістограми
        int[] bins = new int[numBins];
        for (double value : data) {
            int binIndex = (int) ((value - min) / binWidth);
            if (binIndex == numBins) {
                binIndex--;
            }
            bins[binIndex]++;
        }

        for (int i = 0; i < numBins; i++) {
            dataset.addValue((double) bins[i]/data.size(), "Probability" + seriesCounter, String.format("%.2f - %.2f", min + i * binWidth, min + (i + 1) * binWidth));
        }

        // Створення графіку
        JFreeChart chart = ChartFactory.createBarChart3D("Histogram", "Site Power", "Frequency", dataset, PlotOrientation.VERTICAL, true, true, false);

        // Зміна кольору гістограми
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(seriesCounter, getRandomColor());

        seriesCounter++;

        // Оновлення графіку у вікні з можливістю масштабування, переміщення та повороту
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setPopupMenu(null);

        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private static Color getRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r, g, b);
    }*/


    private static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private static int seriesCounter = 0;
    private static JFrame frame;

    public static void plotHistogram(ArrayList<Double> data, int numBins) {
        double min = 0; //Collections.min(data);
        double max = Simulation.MAX_SITE_POWER; //Collections.max(data);
        double binWidth = (max - min) / numBins;

        // Заповнення гістограми
        int[] bins = new int[numBins];
        for (double value : data) {
            int binIndex = (int) ((value - min) / binWidth);
            if (binIndex == numBins) {
                binIndex--;
            }
            bins[binIndex]++;
        }

        for (int i = 0; i < numBins; i++) {
            dataset.addValue((double) bins[i] / data.size(),
                    "" + Simulation.getARRIVAL_RATE_STEP() * (1+seriesCounter) + " EV/h",
                    String.format("%.2f - %.2f", min + i * binWidth, min + (i + 1) * binWidth));
        }

        JFreeChart chart = ChartFactory.createBarChart3D("Site Power Distribution Histogram", "Site Power Intervals", "Probability", dataset, PlotOrientation.VERTICAL, true, true, false);

        // Зміна кольору гістограми
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(seriesCounter, getRandomColor());

        seriesCounter++;

        // Оновлення графіку у вікні з можливістю масштабування, переміщення та повороту
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600)); // бажаний розмір панелі графіка
        chartPanel.setMouseWheelEnabled(true); // масштабувати за допомогою колеса миші
        chartPanel.setDomainZoomable(true); // Масштабує область по осі X
        chartPanel.setRangeZoomable(true); // Масштабує область по осі Y
        chartPanel.setPopupMenu(null); // Вимикає контекстне меню графіку (яке включає поворот)


        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

        if (frame == null) {
            frame = new JFrame("Site Power Distribution Histogram");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(chartPanel);
            frame.pack();
            frame.setVisible(true);
        } else {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(chartPanel);
            frame.revalidate();
            frame.repaint();
        }
    }

    private static Color getRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r, g, b);
    }

    static void resetData() {
        dataset.clear();
        seriesCounter = 0;
    }
}





