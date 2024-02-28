package chargingSite;

import eventSimulation.EventSimulation;
import exceptions.SitePowerExceededException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.jfree.util.ShapeUtilities;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import results.Histogram;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class ChargingSite {
    private ArrayList<ChargingPoint> chargingPoints = new ArrayList<>();
    private int numberOfChargingPoints;
    private ArrayList<Double> chargingPowers = new ArrayList<>();
    private double maxSitePower;
    private SimulationParameters simParameters;
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


    public ChargingSite(SimulationParameters parameters) {
        this.simParameters = parameters;
        this.numberOfChargingPoints = parameters.getNUMBER_OF_SERVERS();
        this.maxSitePower = parameters.getMaxSitePower();
        initializeChargingPoints();
    }

    private void initializeChargingPoints() {
        for (int i = 0; i < numberOfChargingPoints; i++) {
            chargingPowers.add((double) simParameters.MAX_POINT_POWER);
            chargingPoints.add(new ChargingPoint(simParameters.MAX_POINT_POWER));
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

    public static List<TimePowerData> dataList = new ArrayList<>();
    private double previousSitePower = -1;
    private double previousTime = -1;
    private boolean isFirstValue = true;

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
        // sitePowerSeries.add(EventSimulation.getCurrentTime(), sitePower);

        if (!isFirstValue && (EventSimulation.getCurrentTime() != previousTime || sitePower != previousSitePower)) {
            dataList.add(new TimePowerData(EventSimulation.getCurrentTime(), sitePower));
            addSitePower(sitePower);
        }

        previousTime = EventSimulation.getCurrentTime();
        previousSitePower = sitePower;
        isFirstValue = false;

        return sitePower;
    }

    private XYSeries sitePowerSeries = new XYSeries("Site Power");

    public void addSitePower1(double sitePower) {
        double currentTime = EventSimulation.getCurrentTime();
        sitePowerSeries.add(currentTime, sitePower);
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
            }

        } else {
            System.out.println("No non-zero data available for histogram.");
        }
    }

    private static boolean isWindowClosing = false;

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

    private static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private static int seriesCounter = 0;
    public static JFrame frame;

    public static void plotHistogram(ArrayList<Double> data, int numBins, SimulationParameters parameters) {
        double min = 0; //Collections.min(data);
        double max = parameters.MAX_SITE_POWER; //Collections.max(data);
        double binWidth = (max - min) / numBins;

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
                    "" + parameters.getARRIVAL_RATE_STEP() * (1 + seriesCounter) + " EV/h",
                    String.format("%.2f - %.2f", min + i * binWidth, min + (i + 1) * binWidth));
        }

        JFreeChart chart = ChartFactory.createBarChart3D("Site Power Distribution Histogram", "Site Power Intervals", "Probability", dataset, PlotOrientation.VERTICAL, true, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(seriesCounter, getRandomColor());

        seriesCounter++;

        ChartPanel chartPanel = new ChartPanel(chart);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice largestScreen = null;
        Rectangle largestBounds = new Rectangle();
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            if ((largestScreen == null) || (bounds.getWidth() * bounds.getHeight() > largestBounds.getWidth() * largestBounds.getHeight())) {
                largestScreen = gd;
                largestBounds = bounds;
            }
        }

        int frameWidth = (int) (largestBounds.width * 0.32);
        int frameHeight = (int) (largestBounds.height * 0.44);
        int frameX = (int) (largestBounds.x + largestBounds.width - frameWidth - (largestBounds.width * 0.02));
        int frameY = (int) (largestBounds.y + largestBounds.height - frameHeight - largestBounds.height * 0.09);

        chartPanel.setPreferredSize(new Dimension(frameWidth, frameHeight));

        // chartPanel.setPreferredSize(new Dimension(640, 590));
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setPopupMenu(null);


        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

        if (frame == null) {
            frame = new JFrame("Site Power Distribution Histogram");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(chartPanel);
            frame.pack();
            frame.setLocation(frameX, frameY);
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

    public static void saveHistogramToSVG(String filePath) {
        int width = 1400;
        int height = 700;
        // Переконуємося, що frame та chartPanel існують
        if (frame == null || frame.getContentPane().getComponentCount() == 0) {
            System.err.println("Гістограма ще не була створена або відображена.");
            return;
        }
        // Створюємо SVG контекст для малювання з вказаними розмірами
        SVGGraphics2D g2 = new SVGGraphics2D(width, height);

        // Отримуємо chartPanel для доступу до графіка
        ChartPanel chartPanel = (ChartPanel) frame.getContentPane().getComponent(0);
        // Використовуємо метод draw з адаптованими розмірами
        chartPanel.getChart().draw(g2, new Rectangle(0, 0, width, height));

        // Записуємо SVG у файл
        try {
            SVGUtils.writeToSVG(new File(filePath), g2.getSVGElement());
            System.out.println("Гістограма успішно збережена у форматі SVG з розмірами 1400x700.");
        } catch (IOException e) {
            System.err.println("Помилка при збереженні гістограми у форматі SVG: " + e.getMessage());
        }
    }
    /* public static void saveHistogramToSVG(String filePath) {
    if (frame == null || frame.getContentPane().getComponentCount() == 0) {
            System.err.println("Гістограма ще не була створена або відображена.");
            return;
        }
        // Визначаємо розмір SVG на основі розмірів ChartPanel
        ChartPanel chartPanel = (ChartPanel) frame.getContentPane().getComponent(0);
        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();

        // Створюємо SVG контекст для малювання
        SVGGraphics2D g2 = new SVGGraphics2D(width, height);
        // Отримуємо графік з chartPanel та відображаємо його на SVGGraphics2D об'єкті
        chartPanel.getChart().draw(g2, new Rectangle(width, height));

        // Записуємо створений SVG у файл
        try {
            SVGUtils.writeToSVG(new File(filePath), g2.getSVGElement());
            System.out.println("Гістограма успішно збережена у форматі SVG.");
        } catch (IOException e) {
            System.err.println("Помилка при збереженні гістограми у форматі SVG: " + e.getMessage());
        }
    }*/

    private static JFrame frame1;
    private static ChartPanel chartPanel1;
    private static XYSeriesCollection dataset1;
    private static Color[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.BLACK};

    public static void initializeChart1() {

        frame1 = new JFrame();
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dataset1 = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Power vs Time Chart",
                "Time",
                "Power",
                dataset1,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chartPanel1 = new ChartPanel(chart);
        frame1.add(chartPanel1);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        GraphicsDevice largestScreen = gs[0];
        Rectangle largestBounds = largestScreen.getDefaultConfiguration().getBounds();
        for (GraphicsDevice gd : gs) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            if (bounds.getWidth() * bounds.getHeight() > largestBounds.getWidth() * largestBounds.getHeight()) {
                largestScreen = gd;
                largestBounds = bounds;
            }
        }

        int frameWidth = (int) (largestBounds.width * 0.325);
        int frameHeight = (int) (largestBounds.height * 0.47);

        int offsetX = (int) (largestBounds.width * 0.015);

        frame1.setSize(frameWidth, frameHeight);
        frame1.setLocation(largestBounds.x + largestBounds.width - frameWidth - offsetX, largestBounds.y);

        frame1.setVisible(true);
    }


    static void resetData() {
        dataset.clear();
        seriesCounter = 0;
    }

    public static void clearDataset1() {
        if (dataset1 != null) {
            dataset1.removeAllSeries();
            dataList.clear();
        }
    }

    public static void displayChart(List<TimePowerData> dataList, SimulationParameters parameters) {
        if (frame1 == null || chartPanel1 == null || dataset1 == null) {
            initializeChart1();
        }

        double maxTime = parameters.getMaxEvents() / parameters.getMaxArrivalRate() / 100;
        double arrivalRate = (dataset1.getSeriesCount() + 1) * parameters.getMaxArrivalRate() / parameters.getSimSteps();

        XYSeries series = new XYSeries(String.format("%.1f EV/h", arrivalRate));
        for (TimePowerData data : dataList) {
            if (data.getTime() > maxTime && data.getTime() <= 2 * maxTime) {
                series.add(data.getTime(), data.getPower());
            }
        }
        double progress = (double) dataset1.getSeriesCount() / parameters.getSIM_STEPS();
        int R = 0;
        int G = (int) Math.floor(255 * progress);
        int B = 255;
        Shape cross = ShapeUtilities.createDiagonalCross(2.1f, 0.15f); //.createRegularCross(1, 1);.createDiamond(2.1f);

        dataset1.addSeries(series);
        XYPlot plot = (XYPlot) chartPanel1.getChart().getPlot();

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesLinesVisible(dataset1.getSeriesCount() - 1, false);
        renderer.setSeriesShapesVisible(dataset1.getSeriesCount() - 1, true);
        renderer.setSeriesShape(dataset1.getSeriesCount() - 1, cross);
        plot.getRenderer().setSeriesPaint(dataset1.getSeriesCount() - 1, new Color(R, G, B));

        plot.getRangeAxis().setRange(0, parameters.MAX_SITE_POWER * 1.05);

        frame1.repaint();
    }
}