package chargingSite;

import com.orsoncharts.*;
import com.orsoncharts.data.xyz.XYZSeries;
import com.orsoncharts.data.xyz.XYZSeriesCollection;
import com.orsoncharts.graphics3d.ViewPoint3D;
import com.orsoncharts.legend.LegendAnchor;
import com.orsoncharts.legend.StandardLegendBuilder;
import com.orsoncharts.plot.XYZPlot;

import com.orsoncharts.util.Orientation;
import eventSimulation.EventSimulation;
import exceptions.SitePowerExceededException;
import simulationParameters.SimulationParameters;

import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.jfree.util.ShapeUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class ChargingSite {

    // Charging site functionality
    private ArrayList<ChargingPoint> chargingPoints = new ArrayList<>();
    private int numberOfChargingPoints;
    private ArrayList<Double> chargingPowers = new ArrayList<>();
    private double maxSitePower;
    private ArrayList<Double> sitePowerList = new ArrayList<>();

    private static SimulationParameters simParameters;

    public ArrayList<Double> getSitePowerList() {
        return sitePowerList;
    }

    // Diagrams and charts functionality
    public static List<TimePowerData> powerDataList = new ArrayList<>();
    private XYSeries sitePowerSeries = new XYSeries("Site Power");


    private static Color[] colors; // colour array to be initialised with n = number of simulation steps
    private static Color[] initColors(int n) {
        return initColors(n, Colors.LOW_BLUE, Colors.HIGH_RED);
    }
    private static Color[] initColors(int n, Color c1, Color c2) {
        colors = new Color[n];
        int R, G, B;
        double p;
        for (int i = 0; i < n; i++) {
            p = (double) i / (n - 1);
            R = (int) Math.round((1 - p) * c1.getRed() + p * c2.getRed());
            G = (int) Math.round((1 - p) * c1.getGreen() + p * c2.getGreen());
            B = (int) Math.round((1 - p) * c1.getBlue() + p * c2.getBlue());
            colors[i] = new Color(R, G, B);
        }
        return colors;
    }

    public ChargingSite(SimulationParameters parameters) {
        this.simParameters = parameters;
        this.numberOfChargingPoints = parameters.getNUMBER_OF_SERVERS();
        this.maxSitePower = parameters.getMaxSitePower();
        initializeChargingPoints();

        initColors(parameters.getSteps());
    }

    private void initializeChargingPoints() {
        for (int i = 0; i < numberOfChargingPoints; i++) {
            chargingPowers.add((double) simParameters.MAX_POINT_POWER);
            chargingPoints.add(new ChargingPoint(simParameters.MAX_POINT_POWER));
        }
    }

    public void resetPowerRecords() {
        sitePowerSeries.clear();
        sitePowerList.clear();
        powerDataList.clear();
    }

    public ChargingPoint getChargingPoint(int index) {
        return this.chargingPoints.get(index);
    }

    public ChargingPoint getIdleChargingPoint(PlugType plugType) {
        for (ChargingPoint next : chargingPoints) {
            if (next.getChargedCar() == null)
                return next;
        }
        return null;
    }

    public double getMaxSitePower() {
        return maxSitePower;
    }

    public void checkPower() {
        getSitePower();
    }

    public void executeClockTick(double timeScale) {
        // do something -> record system states...
        double power = getSitePower();
        powerDataList.add(new TimePowerData(EventSimulation.getCurrentTime() * timeScale, power));
        addSitePower(power);
    }

    public double getSitePower() {
        double sitePower = 0;
        int i = 0;

        for (ChargingPoint next : chargingPoints) {
            sitePower += next.getPower();
            i++;
        }
        if (i > numberOfChargingPoints) {
            throw new IllegalStateException("<ChargingSite>.getSitePower summed over " + i + " chargingPoints > "
                    + this.numberOfChargingPoints + " configured!");
        }
        if (sitePower > maxSitePower) {
            sitePower = scaleChargingPower(maxSitePower / sitePower);
            if (sitePower - maxSitePower > 0.0001) {
                throw new SitePowerExceededException(
                        "Site power " + sitePower + " is greater than the set maximum " + maxSitePower + " !");
            }
        }
        return sitePower;
    }

    public void addSitePowerSeries(double sitePower) {
        double currentTime = EventSimulation.getCurrentTime();
        sitePowerSeries.add(currentTime, sitePower);
    }

    private void addSitePower(double power) {
        sitePowerList.add(power);
    }

    public double scaleChargingPower(double scale) {
        double newSitePower = 0;

        for (ChargingPoint next : chargingPoints) {
            ElectricVehicle car = next.getChargedCar();
            if (car != null)
                newSitePower += car.scaleChargingPower(scale);
        }
        return newSitePower;
    }

    private double[] convertXYSeriesToDoubleArray(XYSeries series) {
        double[] data = new double[series.getItemCount()];
        for (int i = 0; i < series.getItemCount(); i++) {
            data[i] = series.getY(i).doubleValue();
        }
        return data;
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

    static JFrame powerOverTimeFrame;
    private static ChartPanel powerOverTimeChart;
    private static XYSeriesCollection powerOverTimeDataset;


    public static void initializePowerOverTimeChart(boolean withGui) {
        if (powerOverTimeFrame != null) {
            powerOverTimeFrame.setVisible(withGui);
        } else {
            createPowerOverTimeChart(withGui);
        }
    }

    public static void createPowerOverTimeChart(boolean withGui) {
        powerOverTimeFrame = new JFrame();
        powerOverTimeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        powerOverTimeFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                promptSaveOnClosePowerOverTime();
            }
        });

        powerOverTimeDataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Charging site Power over Time",
                "Time [h]",
                "Power [kW]",
                powerOverTimeDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        chart.getLegend().setVisible(false);
        powerOverTimeChart = new ChartPanel(chart);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(powerOverTimeChart, BorderLayout.CENTER);

        JButton toggleLegendButton = new JButton("Toggle Legend");
        toggleLegendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean legendVisibility = chart.getLegend().isVisible();
                chart.getLegend().setVisible(!legendVisibility);
                powerOverTimeChart.repaint();
            }
        });

        panel.add(toggleLegendButton, BorderLayout.SOUTH);

        powerOverTimeFrame.getContentPane().add(panel);

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

        int frameWidth = (int) (largestBounds.width * 0.332);
        int frameHeight = (int) (largestBounds.height * 0.473);

        int offsetX = (int) (largestBounds.width * 0.015);

        powerOverTimeFrame.setSize(frameWidth, frameHeight);
        powerOverTimeFrame.setLocation(largestBounds.x + largestBounds.width - frameWidth - offsetX, largestBounds.y);

        powerOverTimeFrame.setVisible(withGui);
    }

    private static void promptSaveOnClosePowerOverTime() {
        Object[] options = { "Save", "Cancel", "Close" };
        int choice = JOptionPane.showOptionDialog(powerOverTimeFrame, "Do you want to save changes or close?", "Save or Close",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case JOptionPane.YES_OPTION:
                showPowerOverTimeSaveOptions();
                break;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CANCEL_OPTION:
                powerOverTimeFrame.dispose();
                break;
            default:
                break;
        }
    }

    public static void composePowerOverTimeChart(List<TimePowerData> dataList, SimulationParameters parameters) {
        double maxTime;
        maxTime = 1 + 25 * parameters.getMaxArrivalRate() / parameters.getMaxEvents();
        double arrivalRate;
        arrivalRate = (powerOverTimeDataset.getSeriesCount() + 1) * parameters.getMaxArrivalRate() / parameters.getSimSteps();

        XYSeries series = new XYSeries(String.format("%.1f EV/h", arrivalRate));
        for (TimePowerData data : dataList) {
            if (data.getTime() > maxTime && data.getTime() <= 2 * maxTime) {
                series.add(data.getTime(), data.getPower());
            }
        }
        powerOverTimeDataset.addSeries(series);
    }

    public static void displayPowerOverTimeChart(List<TimePowerData> dataList, SimulationParameters parameters, Boolean runWithGUI) {
        if (powerOverTimeFrame == null || powerOverTimeChart == null || powerOverTimeDataset == null) {
            createPowerOverTimeChart(runWithGUI);
        }
        if (colors == null)
            colors = initColors(parameters.getSteps());

        Shape cross = ShapeUtilities.createDiagonalCross(2.1f, 0.15f);

        XYPlot plot = (XYPlot) powerOverTimeChart.getChart().getPlot();

        plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD); //would be nice

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        for(int i = 0 ; i < powerOverTimeDataset.getSeriesCount(); i++ ) {
            renderer.setSeriesLinesVisible(i, false);
            renderer.setSeriesShapesVisible(i, true);
            renderer.setSeriesShape(i, cross);
            plot.getRenderer().setSeriesPaint(i, colors[i]);
        }
        plot.getRangeAxis().setRange(0, parameters.MAX_SITE_POWER * 1.05);

        powerOverTimeFrame.repaint();
    }

    private static void showPowerOverTimeSaveOptions() {
        Object[] saveOptions = { "CSV", "SVG", "PNG" };
        int formatChoice = JOptionPane.showOptionDialog(powerOverTimeFrame, "Choose the format to save the chart:",
                "Save Chart Format",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, saveOptions, saveOptions[0]);

        if (formatChoice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String fileExtension = formatChoice == 0 ? ".csv" : formatChoice == 1 ? ".svg" : ".png";
        String defaultFileName = "powerOverTimeChart" + fileExtension;

        int width = 1000, height = 1000;

        if (formatChoice == 1 || formatChoice == 2) {
            String defaultSize = formatChoice == 1 ? "1200x730" : "2400x1560";
            String sizeInput = JOptionPane.showInputDialog(powerOverTimeFrame, "Enter dimensions (width x height):", defaultSize);
            if (sizeInput == null) {
                return;
            }
            String[] sizes = sizeInput.split("x");
            try {
                width = Integer.parseInt(sizes[0].trim());
                height = Integer.parseInt(sizes[1].trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(powerOverTimeFrame, "Invalid dimensions. Using default values.");
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select directory and filename to save");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setSelectedFile(new File(defaultFileName)); // Пропонуємо назву файлу

        if (fileChooser.showSaveDialog(powerOverTimeFrame) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getAbsolutePath().endsWith(fileExtension)) {
                fileToSave = new File(fileToSave.getAbsolutePath() + fileExtension);
            }

            try {
                switch (formatChoice) {
                    case 0:
                        savePowerOverTimeGraphAsCSV(fileToSave.getAbsolutePath());
                        break;
                    case 1:
                        savePowerOverTimeToSVG(fileToSave.getAbsolutePath(), width, height);
                        break;
                    case 2:
                        savePowerOverTimeGraphToPNG(fileToSave.getAbsolutePath(), width, height);
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(powerOverTimeFrame, "Error saving file: " + e.getMessage(), "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String formatDouble(DecimalFormat df, Double value) {
        return df.format(value);
    }

    public static void savePowerOverTimeGraphAsCSV(String filePath) {
        DecimalFormat df = new DecimalFormat("#.####################");
        df.setDecimalSeparatorAlwaysShown(false);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Time;Power\n");

            for (int i = 0; i < powerOverTimeDataset.getSeriesCount(); i++) {
                XYSeries series = powerOverTimeDataset.getSeries(i);
                for (int j = 0; j < series.getItemCount(); j++) {
                    double time = (double) series.getX(j);
                    Number power = series.getY(j);
                    if (time > 1.0 && time <= 2.0) {
                        writer.append(formatDouble(df, time))
                                .append(";")
                                .append(formatDouble(df, power.doubleValue()))
                                .append("\n");
                    }

                }
            }

            System.out.println("CSV file has been created successfully!");
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }

    public static void savePowerOverTimeToSVG(String filePath) {
        savePowerOverTimeToSVG(filePath, DefaultPictureSizes.SVG_WIDTH, DefaultPictureSizes.SVG_HEIGTH);
    }

    public static void savePowerOverTimeToSVG(String filePath, int width, int height) {
        SVGGraphics2D g2 = new SVGGraphics2D(width, height);
        Rectangle r = new Rectangle(0, 0, width, height);
        powerOverTimeChart.getChart().draw(g2, r);
        try {
            SVGUtils.writeToSVG(new File(filePath), g2.getSVGElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePowerOverTimeGraphAsPNG(String filePath) {
        savePowerOverTimeGraphToPNG(filePath, DefaultPictureSizes.PNG_WIDTH, DefaultPictureSizes.PNG_HEIGTH);
    }

    public static void savePowerOverTimeGraphToPNG(String filePath, int width, int height) {
        if (powerOverTimeChart != null && powerOverTimeChart.getChart() != null) {
            try {
                ChartUtils.saveChartAsPNG(new File(filePath), powerOverTimeChart.getChart(), width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Chart or ChartPanel is null.");
        }
    }

    private static DefaultCategoryDataset histogramDataset = new DefaultCategoryDataset();
    private static int seriesCounter = 0;
    public static JFrame histogramFrame;
    private static JPanel histogramPanel;

    public static void initializeHistogram(boolean withGui) {
        if (histogramFrame != null) {
            histogramFrame.setVisible(withGui);
        } else {
            createHistogramFrame(withGui);
        }
    }

    public static void createHistogramFrame(Boolean runWithGUI) {
        histogramFrame = new JFrame("Charging site Power Histogram");
        histogramFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        histogramFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                promptSaveOnCloseHistogram();
            }
        });
        histogramPanel = new JPanel(new BorderLayout());
        histogramFrame.setContentPane(histogramPanel);

        JButton toggleLegendButton = new JButton("Toggle Legend");
        toggleLegendButton.addActionListener(e -> {

            ChartPanel chartPanel = (ChartPanel) histogramPanel.getComponent(1);
            JFreeChart chart = chartPanel.getChart();
            chart.getLegend().setVisible(!chart.getLegend().isVisible());
            chartPanel.repaint();
        });
        histogramPanel.add(toggleLegendButton, BorderLayout.SOUTH);

        histogramFrame.setSize(getPreferredFrameSize());
        histogramFrame.setLocation(getPreferredFrameLocation());
        histogramFrame.setVisible(runWithGUI);
    }

    public static void plotHistogram(ArrayList<Double> data, int numBins, SimulationParameters parameters, Boolean runWithGUI) {
        if (histogramFrame == null) {
            createHistogramFrame(runWithGUI);
        }
        updateDataset(data, numBins, parameters);
        JFreeChart chart = createHistogramChart();

        ChartPanel chartPanel = new ChartPanel(chart);
        configureChartPanel(chartPanel);

        if (histogramPanel.getComponentCount() > 1) {
            histogramPanel.remove(1);
        }
        histogramPanel.add(chartPanel, BorderLayout.CENTER);
        histogramPanel.revalidate();
        histogramPanel.repaint();
    }

    private static void updateDataset(ArrayList<Double> data, int numBins, SimulationParameters parameters) {
        double min = 0;
        double max = parameters.MAX_SITE_POWER;
        double binWidth = (max - min) / numBins;

        int[] bins = new int[numBins];
        for (double value : data) {
            int binIndex = Math.min((int) ((value - min) / binWidth), numBins - 1);
            bins[binIndex]++;
        }

        for (int i = 0; i < numBins; i++) {
            String label = String.format("%.1f EV/h", parameters.getArrivalRateStep() * (1 + seriesCounter));
            histogramDataset.addValue((double) bins[i] / data.size(),
                    label,
                    String.format("%.2f - %.2f", min + i * binWidth, min + (i + 1) * binWidth));
        }
        seriesCounter++;
    }

    private static JFreeChart createHistogramChart() {
        JFreeChart chart = ChartFactory.createBarChart3D(
                "Charging site Power Histogram",
                "Site Power Intervals",
                "Probability",
                histogramDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        CategoryPlot plot = chart.getCategoryPlot();
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(seriesCounter - 1, getRandomColor());

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        chart.getLegend().setVisible(false);
        return chart;
    }

    private static void configureChartPanel(ChartPanel chartPanel) {
        chartPanel.setPreferredSize(new Dimension(histogramFrame.getWidth(), histogramFrame.getHeight()));
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setPopupMenu(null);
    }

    private static Dimension getPreferredFrameSize() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle largestBounds = new Rectangle();
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            if (bounds.getWidth() * bounds.getHeight() > largestBounds.getWidth() * largestBounds.getHeight()) {
                largestBounds = bounds;
            }
        }
        int frameWidth = (int) (largestBounds.width * 0.33);
        int frameHeight = (int) (largestBounds.height * 0.47);
        return new Dimension(frameWidth, frameHeight);
    }

    private static Point getPreferredFrameLocation() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle largestBounds = new Rectangle();
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            if ((largestBounds.isEmpty()) || (bounds.getWidth() * bounds.getHeight() > largestBounds.getWidth()
                    * largestBounds.getHeight())) {
                largestBounds = bounds;
            }
        }
        Dimension frameSize = getPreferredFrameSize();
        int frameX = (int) (largestBounds.x + largestBounds.width - frameSize.width
                - (largestBounds.width * 0.016));

        int frameY = (int) (largestBounds.y + (largestBounds.height - frameSize.height) * 0.5
                + largestBounds.height * 0.205);

        return new Point(frameX, frameY);
    }
    static JFrame histogram3dFrame = null;
    private static Chart3D histogram3dChart;
    private static boolean legendVisible = false;
    private static XYZSeriesCollection<String> histogram3dDataset = new XYZSeriesCollection<>();

    public static void plotHistogram3D(double arrivalRate, ArrayList<Double> data, int numBins,
                                       SimulationParameters parameters, Boolean runWithGUI) {

        if(histogram3dFrame == null) {
            histogram3dFrame = new JFrame("Charging site Power Histogram");
            histogram3dFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            histogram3dFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    //promptSaveOnCloseHistogram3D();
                }
            });
        }

        double min = 0;
        double max = parameters.MAX_SITE_POWER;
        double binWidth = (max - min) / numBins;
        if (colors == null) {
            initColors(parameters.getSteps(), new Color(0, 0, 255), new Color(255, 0, 127));
        }

        XYZSeries<String> series = new XYZSeries<>(String.format("%.1f EV/h", arrivalRate));
        int[] bins = new int[numBins];
        for (double value : data) {
            int binIndex = Math.min((int) ((value - min) / binWidth), numBins - 1);
            bins[binIndex]++;
        }

        double binSum = Arrays.stream(bins).sum();
        for (int i = 0; i < numBins; i++) {
            double xValue = min + i * binWidth + binWidth / 2;
            double yValue = arrivalRate;
            double zValue = bins[i] / binSum;
            series.add(xValue, yValue, zValue);
        }
        histogram3dDataset.add(series);

        histogram3dChart = Chart3DFactory.createXYZLineChart("Charging site Power Histogram", "", histogram3dDataset, "Site Power [kW]",
                "Arrival Rate [EV/h]", "Probability Mass");
        updateLegendVisibility();
        histogram3dChart.setLegendOrientation(Orientation.VERTICAL);
        histogram3dChart.setLegendAnchor(LegendAnchor.TOP_RIGHT);

        XYZPlot plot3D = (XYZPlot) histogram3dChart.getPlot();
        histogram3dChart.setChartBoxColor(Color.white);
        plot3D.getRenderer().setColors(colors);
        histogram3dChart.setLegendBuilder(null);
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        histogram3dChart.setTitle("Charging site power Histogram", titleFont, Color.black);
        histogram3dChart.setTitleAnchor(TitleAnchor.TOP_CENTER);

        Chart3DPanel chartPanel = new Chart3DPanel(histogram3dChart);
        ViewPoint3D viewPoint = new ViewPoint3D(-0.775, -1.425, calculateOptimalViewAngleForLargestScreen(), 0);
        chartPanel.setViewPoint(viewPoint);

        if (histogram3dFrame.getContentPane().getComponentCount() > 0) {
            histogram3dFrame.getContentPane().removeAll();
        }

        histogram3dFrame.add(chartPanel);
        adjustFrameToScreen();
        histogram3dFrame.validate();
        addLegendToggle();
        histogram3dFrame.repaint();
        histogram3dFrame.setVisible(runWithGUI);
    }

    private static double calculateOptimalViewAngleForLargestScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        double maxDiagonal = 0;
        Dimension maxScreenSize = new Dimension();

        for (GraphicsDevice screen : screens) {
            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            double screenWidth = bounds.getWidth();
            double screenHeight = bounds.getHeight();
            double diagonal = Math.sqrt(screenWidth * screenWidth + screenHeight * screenHeight);

            if (diagonal > maxDiagonal) {
                maxDiagonal = diagonal;
                maxScreenSize.setSize(screenWidth, screenHeight);
            }
        }
        double screenDiagonal = Math
                .sqrt(maxScreenSize.width * maxScreenSize.width + maxScreenSize.height * maxScreenSize.height)
                / Toolkit.getDefaultToolkit().getScreenResolution();
        double angleAdjustmentPerInch;
        if (screenDiagonal < 17) {
            angleAdjustmentPerInch = -15.0 / 5.0;
        } else {

            angleAdjustmentPerInch = -2.0 / 5.0;
        }
        double baseAngle = 45;
        double diagonalDifference = screenDiagonal - 24;
        double angle = baseAngle + diagonalDifference * angleAdjustmentPerInch;

        return angle;
    }

    public static void adjustFrameToScreen() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] allDevices = env.getScreenDevices();
        double maxArea = 0;
        Rectangle largestBounds = null;

        for (GraphicsDevice screen : allDevices) {
            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            double area = bounds.getWidth() * bounds.getHeight();
            if (area > maxArea) {
                maxArea = area;
                largestBounds = bounds;
            }
        }

        if (largestBounds != null) {
            int frameWidth = (int) (largestBounds.width * 0.33);
            int frameHeight = (int) (largestBounds.height * 0.47);
            histogram3dFrame.setSize(frameWidth, frameHeight);

            int xPosition = largestBounds.x + largestBounds.width - frameWidth;
            int yPosition = largestBounds.y + largestBounds.height - frameHeight;

            yPosition -= frameHeight * 0.13;
            xPosition -= frameWidth * 0.05;

            histogram3dFrame.setLocation(xPosition, yPosition);
        }
    }

    private static void addLegendToggle() {
        JToggleButton toggleButton = new JToggleButton("Toggle Legend");
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legendVisible = !legendVisible;
                updateLegendVisibility();
                histogram3dFrame.repaint();
            }
        });

        histogram3dFrame.getContentPane().add(toggleButton, BorderLayout.SOUTH); // Додавання кнопки в нижню частину вікна
    }

    private static void updateLegendVisibility() {
        if (histogram3dChart != null) {
            if (legendVisible) {
                StandardLegendBuilder legendBuilder = new StandardLegendBuilder();
                histogram3dChart.setLegendBuilder(legendBuilder);
            } else {
                histogram3dChart.setLegendBuilder(null);
            }
        }
    }

    public void saveHistogram3DAsCSV(String filePath) {
        DecimalFormat df = new DecimalFormat("#.######################");
        df.setDecimalSeparatorAlwaysShown(false);

        try {
            try (FileWriter csvWriter = new FileWriter(filePath)) {
                csvWriter.append("Arrival Rate");

                int seriesCount = histogram3dDataset.getSeriesCount();

                int maxItemCount = 0;
                for (int i = 0; i < seriesCount; i++) {
                    int itemCount = histogram3dDataset.getSeries(i).getItemCount();
                    if (itemCount > maxItemCount) {
                        maxItemCount = itemCount;
                    }
                }
                // first line of csv file
                for (int column = 0; column < maxItemCount; column++) {
                    double binWidth = simParameters.MAX_SITE_POWER / maxItemCount;
                    String binRange = column * binWidth + "-" + (column + 1) * binWidth;
                    csvWriter.append("; ").append(binRange);
                }
                csvWriter.append("\n");

                // following lines of csv file
                for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                    XYZSeries<String> series = histogram3dDataset.getSeries(seriesIndex);
                    csvWriter.append(series.getKey());

                    for (int itemIndex = 0; itemIndex < series.getItemCount(); itemIndex++) {
                        double zValue = series.getZValue(itemIndex);

                        csvWriter.append(";").append(formatDouble(df, zValue));
                    }
                    csvWriter.append("\n");
                }
                csvWriter.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveHistogram3DAsPNG(String filePath) {
        if (histogram3dFrame.getContentPane().getComponentCount() > 0
                && histogram3dFrame.getContentPane().getComponent(0) instanceof Chart3DPanel) {
            Chart3DPanel chartPanel = (Chart3DPanel) histogram3dFrame.getContentPane().getComponent(0);
            int originalWidth = chartPanel.getWidth();
            int originalHeight = chartPanel.getHeight();

            if(originalWidth == 0 || originalHeight == 0) {
                originalWidth = 1700;//? - default size
                originalHeight = 720;//? - default size
                chartPanel.setSize(originalWidth, originalHeight);
            }

            int width = originalWidth * 3;
            int height = originalHeight * 3;
            double scaleX = 3.0;
            double scaleY = 3.0;


            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            g2.scale(scaleX, scaleY);
            chartPanel.paint(g2);
            g2.dispose();

            try {
                ImageIO.write(image, "png", new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Не знайдено Chart3DPanel.");
        }
    }

    public static void saveHistogram3DToPNG(String filePath, int width, int height) {
        if (histogram3dFrame.getContentPane().getComponentCount() > 0
                && histogram3dFrame.getContentPane().getComponent(0) instanceof Chart3DPanel) {
            Chart3DPanel chartPanel = (Chart3DPanel) histogram3dFrame.getContentPane().getComponent(0);

            // Масштабування зображення залежно від заданих параметрів ширини та висоти
            double scaleX = (double) width / chartPanel.getWidth();
            double scaleY = (double) height / chartPanel.getHeight();

            // Використання заданих параметрів ширини та висоти для створення зображення
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            g2.scale(scaleX, scaleY);
            chartPanel.paint(g2);
            g2.dispose();

            try {
                ImageIO.write(image, "png", new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Не знайдено Chart3DPanel.");
        }
    }

    public static void saveHistogram3DToCSV(String filePath) {

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.append("X Value,Y Value,Z Value\n");
            for (int seriesIndex = 0; seriesIndex < histogram3dDataset.getSeriesCount(); seriesIndex++) {
                XYZSeries<String> series = histogram3dDataset.getSeries(seriesIndex);

                for (int itemIndex = 0; itemIndex < series.getItemCount(); itemIndex++) {
                    double xValue = series.getXValue(itemIndex);
                    double yValue = series.getYValue(itemIndex);
                    double zValue = series.getZValue(itemIndex);

                    writer.append(String.format("%f,%f,%f\n", xValue, yValue, zValue));
                }
            }

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveHistogram3DAsSVG(String filePath) {
        int width = 1200;
        int height = 720;
        saveHistogram3DAsSVG(filePath, width, height);
    }

    public static void saveHistogram3DAsSVG(String filePath, int width, int height) {
        if (histogram3dFrame.getContentPane().getComponentCount() > 0
                && histogram3dFrame.getContentPane().getComponent(0) instanceof Chart3DPanel) {
            Chart3DPanel chartPanel = (Chart3DPanel) histogram3dFrame.getContentPane().getComponent(0);

            SVGGraphics2D g2 = new SVGGraphics2D(width, height);

            chartPanel.getChart().draw(g2, new Rectangle(0, 0, width, height));

            try {
                SVGUtils.writeToSVG(new File(filePath), g2.getSVGElement());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Не знайдено Chart3DPanel.");
        }
    }

    private static void promptSaveOnCloseHistogram() {
        Object[] options = { "Save", "Cancel", "Close" };
        int choice = JOptionPane.showOptionDialog(histogramFrame, "Do you want to save changes or close?", "Save or Close",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case JOptionPane.YES_OPTION:
                showSaveOptionsHistogram();
                break;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CANCEL_OPTION:
                histogramFrame.setVisible(false);
                // frame.dispose();
                break;
            default:
                break;
        }
    }

    private static Color getRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r, g, b);
    }

    private static void showSaveOptionsHistogram() {

        Object[] saveOptions = { "CSV", "SVG", "PNG" };
        int formatChoice = JOptionPane.showOptionDialog(histogramFrame, "Choose the format to save the chart:",
                "Save Chart Format",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, saveOptions, saveOptions[0]);

        if (formatChoice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String fileExtension = formatChoice == 0 ? ".csv" : formatChoice == 1 ? ".svg" : ".png";
        String defaultFileName = "HistogramChart" + fileExtension;

        int width = 1000, height = 1000;

        if (formatChoice == 1 || formatChoice == 2) {
            String defaultSize = formatChoice == 1 ? "1200x730" : "2400x1560";
            String sizeInput = JOptionPane.showInputDialog(histogramFrame, "Enter dimensions (width x height):", defaultSize);
            if (sizeInput == null) {
                return;
            }
            String[] sizes = sizeInput.split("x");
            try {
                width = Integer.parseInt(sizes[0].trim());
                height = Integer.parseInt(sizes[1].trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(histogramFrame, "Invalid dimensions. Using default values.");
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select directory and filename to save");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setSelectedFile(new File(defaultFileName)); // Пропонуємо назву файлу

        if (fileChooser.showSaveDialog(histogramFrame) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getName().endsWith(fileExtension)) {
                fileToSave = new File(fileToSave.getAbsolutePath() + fileExtension);
            }

            try {
                if (formatChoice == 0) {
                    saveHistogramAsCSV(fileToSave.getAbsolutePath());
                } else if (formatChoice == 1) {
                    saveHistogramAsSVG(fileToSave.getAbsolutePath(), width, height);
                } else if (formatChoice == 2) {
                    saveHistogramAsPNG(fileToSave.getAbsolutePath(), width, height);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(histogramFrame, "Error saving file: " + e.getMessage(), "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void saveHistogramAsSVG(String filePath) {
        saveHistogramAsSVG(filePath, DefaultPictureSizes.SVG_WIDTH, DefaultPictureSizes.SVG_HEIGTH);
    }

    public static void saveHistogramAsSVG(String filePath, int width, int height) {
        if (histogramFrame == null) {
            System.err.println("Histogram frame not initialized");
            return;
        }

        if (histogramPanel.getComponentCount() > 1) {
            ChartPanel chartPanel = (ChartPanel) histogramPanel.getComponent(1);
            JFreeChart chart = chartPanel.getChart();

            SVGGraphics2D svgGraphics = new SVGGraphics2D(width, height);
            Rectangle2D chartArea = new Rectangle2D.Double(0, 0, width, height);

            chart.draw(svgGraphics, chartArea);

            File svgFile = new File(filePath);
            try {
                SVGUtils.writeToSVG(svgFile, svgGraphics.getSVGElement());
                System.out.println("Histogram saved to SVG successfully.");
            } catch (IOException e) {
                System.err.println("Error saving histogram to SVG: " + e.getMessage());
            }
        } else {
            System.err.println("ChartPanel not found in mainPanel");
        }
    }

    public static void saveHistogramAsCSV(String filePath) {
        DecimalFormat df = new DecimalFormat("#.######################");
        df.setDecimalSeparatorAlwaysShown(false);

        try(FileWriter csvWriter = new FileWriter(filePath)) {

            csvWriter.append("Arrival Rate");

            // Отримання кількості рядів та стовпців даних
            int seriesCount = histogramDataset.getRowCount();
            int columnCount = histogramDataset.getColumnCount();

            for (int column = 0; column < columnCount; column++) {
                String binRange = histogramDataset.getColumnKey(column).toString();
                csvWriter.append("; ").append(binRange);
            }

            csvWriter.append("\n");
            for (int series = 0; series < seriesCount; series++) {
                double arrivalRate = (series + 1) * simParameters.getArrivalRateStep();
                csvWriter.append(formatDouble(df, arrivalRate));
                for (int column = 0; column < columnCount; column++) {
                    Number probability = histogramDataset.getValue(series, column);
                    csvWriter.append(";").append(formatDouble(df, probability.doubleValue()));
                }
                csvWriter.append("\n");
            }

            csvWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveHistogramAsPNG(String filePath) {
        saveHistogramAsPNG(filePath, DefaultPictureSizes.PNG_WIDTH, DefaultPictureSizes.PNG_HEIGTH);
    }

    public static void saveHistogramAsPNG(String filePath, int width, int height) {
        if (histogramFrame == null) {
            System.err.println("Histogram frame not initialized");
            return;
        }

        if (histogramPanel.getComponentCount() > 1) {
            ChartPanel chartPanel = (ChartPanel) histogramPanel.getComponent(1);
            JFreeChart chart = chartPanel.getChart();

            try {
                ChartUtilities.saveChartAsPNG(new File(filePath), chart, width, height);
                System.out.println("Histogram saved to PNG successfully.");
            } catch (IOException e) {
                System.err.println("Error saving histogram to PNG: " + e.getMessage());
            }
        } else {
            System.err.println("ChartPanel not found in mainPanel");
        }
    }

    public static void resetFrames(){
        if (histogramFrame != null && histogramDataset != null) {
            histogramDataset.clear();
            seriesCounter = 0;
        }

        if (histogram3dFrame != null && histogram3dDataset != null) {
            histogram3dDataset.removeAll();
            seriesCounter = 0;
        }

        if (powerOverTimeFrame != null && powerOverTimeDataset != null) {
            powerOverTimeDataset.removeAllSeries();

            if(powerDataList != null)
                powerDataList.clear();
        }
    }

    public static void disposeFrames() {
        if (histogramFrame != null) {
            histogramFrame.dispose();
        }

        if (histogram3dFrame != null) {
            histogram3dFrame.dispose();
        }

        if (powerOverTimeFrame != null) {
            powerOverTimeFrame.dispose();
        }
    }
}