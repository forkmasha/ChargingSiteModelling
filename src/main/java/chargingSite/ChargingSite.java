package chargingSite;

import eventSimulation.EventSimulation;
import exceptions.SitePowerExceededException;
import org.jfree.chart.*;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;


public class ChargingSite {
    private ArrayList<ChargingPoint> chargingPoints = new ArrayList<>();
    private int numberOfChargingPoints;
    private ArrayList<Double> chargingPowers = new ArrayList<>();
    private double maxSitePower;
    private static SimulationParameters simParameters;
    public static List<TimePowerData> dataList = new ArrayList<>();
    private double previousSitePower = -1;
    private double previousTime = -1;
    private boolean isFirstValue = true;
    private XYSeries sitePowerSeries = new XYSeries("Site Power");
    private ArrayList sitePower1 = new ArrayList<>();

    public ArrayList getSitePower1() {
        return sitePower1;
    }

    public static Color DARK_BLUE = new Color(0, 0, 139); // Темно-синій
    public static Color LIGHT_BLUE = new Color(173, 216, 230); // Світло-синій


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

    public XYSeries getSitePowerSeries() {
        return sitePowerSeries;
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


    static JFrame frame1;
    private static ChartPanel chartPanel1;
    private static XYSeriesCollection dataset1;

    public static void initializePowerOverTimeChart1() {
        frame1 = new JFrame();
        frame1.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame1.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                promptSaveOnClosePowerOverTime();
            }
        });

        dataset1 = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Power over Time Chart",
                "Time",
                "Power",
                dataset1,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        chart.getLegend().setVisible(false);
        chartPanel1 = new ChartPanel(chart);


        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel1, BorderLayout.CENTER);


        JButton toggleLegendButton = new JButton("Toggle Legend");
        toggleLegendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean legendVisibility = chart.getLegend().isVisible();
                chart.getLegend().setVisible(!legendVisibility);
                chartPanel1.repaint();
            }
        });

        panel.add(toggleLegendButton, BorderLayout.SOUTH);

        frame1.getContentPane().add(panel);

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

  /*  public static void initializePowerOverTimeChart1() {

        frame1 = new JFrame();
        frame1.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame1.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                promptSaveOnClosePowerOverTime();
            }
        });

        dataset1 = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Power over Time Chart",
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
    }*/

    private static void promptSaveOnClosePowerOverTime() {
        Object[] options = {"Save", "Cancel", "Close"};
        int choice = JOptionPane.showOptionDialog(frame1, "Do you want to save changes or close?", "Save or Close",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case JOptionPane.YES_OPTION:
                showPowerOverTimeSaveOptions();
                break;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CANCEL_OPTION:
                frame1.dispose();
                break;
            default:
                break;
        }
    }

    public static void clearPowerOverTimeDataset1() {
        if (dataset1 != null) {
            dataset1.removeAllSeries();
            dataList.clear();
        }
    }

    public static void displayPowerOverTimeChart(List<TimePowerData> dataList, SimulationParameters parameters) {
        if (frame1 == null || chartPanel1 == null || dataset1 == null) {
            initializePowerOverTimeChart1();
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

        // plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);
        plot.setSeriesRenderingOrder(SeriesRenderingOrder.REVERSE);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesLinesVisible(dataset1.getSeriesCount() - 1, false);
        renderer.setSeriesShapesVisible(dataset1.getSeriesCount() - 1, true);
        renderer.setSeriesShape(dataset1.getSeriesCount() - 1, cross);
        plot.getRenderer().setSeriesPaint(dataset1.getSeriesCount() - 1, new Color(R, G, B));

        plot.getRangeAxis().setRange(0, parameters.MAX_SITE_POWER * 1.05);

        frame1.repaint();
    }

    private static void showPowerOverTimeSaveOptions() {
        Object[] saveOptions = {"CSV", "SVG", "PNG"};
        int formatChoice = JOptionPane.showOptionDialog(frame1, "Choose the format to save the chart:", "Save Chart Format",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, saveOptions, saveOptions[0]);

        if (formatChoice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String fileExtension = formatChoice == 0 ? ".csv" : formatChoice == 1 ? ".svg" : ".png";
        String defaultFileName = "powerOverTimeChart" + fileExtension;

        int width = 1000, height = 1000;

        if (formatChoice == 1 || formatChoice == 2) {
            String defaultSize = formatChoice == 1 ? "1200x730" : "2400x1560";
            String sizeInput = JOptionPane.showInputDialog(frame1, "Enter dimensions (width x height):", defaultSize);
            if (sizeInput == null) {
                return;
            }
            String[] sizes = sizeInput.split("x");
            try {
                width = Integer.parseInt(sizes[0].trim());
                height = Integer.parseInt(sizes[1].trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame1, "Invalid dimensions. Using default values.");
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select directory and filename to save");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setSelectedFile(new File(defaultFileName)); // Пропонуємо назву файлу

        if (fileChooser.showSaveDialog(frame1) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getAbsolutePath().endsWith(fileExtension)) {
                fileToSave = new File(fileToSave.getAbsolutePath() + fileExtension);
            }


            try {
                switch (formatChoice) {
                    case 0:
                        savePowerOverTimeGraphToCSV(fileToSave.getAbsolutePath());
                        break;
                    case 1:
                        savePowerOverTimeToSVG(fileToSave.getAbsolutePath(), width, height);
                        break;
                    case 2:
                        savePowerOverTimeGraphToPNG(fileToSave.getAbsolutePath(), width, height);
                        break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame1, "Error saving file: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private static String formatDouble(DecimalFormat df, Double value) {
        return df.format(value);
    }

    public static void savePowerOverTimeGraphToCSV(String filePath) {
        DecimalFormat df = new DecimalFormat("#.####################");
        df.setDecimalSeparatorAlwaysShown(false);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Time;Power\n");

            for (int i = 0; i < dataset1.getSeriesCount(); i++) {
                XYSeries series = dataset1.getSeries(i);
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
    public static JFrame getPowerOverTimeFrame() {
        return frame1;
    }

    public static void savePowerOverTimeToSVG(String filePath) {
        int width = SimulationGUI.WIDTH_OF_SVG_PICTURE;
        int height = SimulationGUI.HEIGHT_OF_SVG_PICTURE;
        SVGGraphics2D g2 = new SVGGraphics2D(width, height);
        Rectangle r = new Rectangle(0, 0, width, height);
        chartPanel1.getChart().draw(g2, r);

        try {
            SVGUtils.writeToSVG(new File(filePath), g2.getSVGElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePowerOverTimeToSVG(String filePath, int width, int height) {
        SVGGraphics2D g2 = new SVGGraphics2D(width, height);
        Rectangle r = new Rectangle(0, 0, width, height);
        chartPanel1.getChart().draw(g2, r);
        try {
            SVGUtils.writeToSVG(new File(filePath), g2.getSVGElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePowerOverTimeGraphToPNG(String filePath) {
        if (chartPanel1 != null && chartPanel1.getChart() != null) {
            try {
                int width = SimulationGUI.WIDTH_OF_PNG_PICTURE;
                int height = SimulationGUI.HEIGHT_OF_PNG_PICTURE;
                File outFile = new File(filePath);
                ChartUtils.saveChartAsPNG(outFile, chartPanel1.getChart(), width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Chart or ChartPanel is null.");
        }
    }

    public static void savePowerOverTimeGraphToPNG(String filePath, int width, int height) {
        if (chartPanel1 != null && chartPanel1.getChart() != null) {
            try {
                ChartUtils.saveChartAsPNG(new File(filePath), chartPanel1.getChart(), width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Chart or ChartPanel is null.");
        }
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
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.getContentPane().add(chartPanel);
            frame.pack();
            frame.setLocation(frameX, frameY);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    promptSaveOnCloseHistogram();
                }
            });

        } else {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(chartPanel);
            frame.revalidate();
            frame.repaint();
        }

       /* if (frame == null) {
           frame = new JFrame("Site Power Distribution Histogram");
           frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
           frame.getContentPane().add(panel);
           frame.pack();
           frame.setLocation(frameX, frameY);
           frame.setVisible(true);
           frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
           frame.addWindowListener(new WindowAdapter() {
               @Override
               public void windowClosing(WindowEvent e) {
                   promptSaveOnCloseHistogram();
               }
           });
       } else {
           frame.getContentPane().removeAll();
           frame.getContentPane().add(panel);
           frame.revalidate();
           frame.repaint();
       }*/
    }

    private static void promptSaveOnCloseHistogram() {
        Object[] options = {"Save", "Cancel", "Close"};
        int choice = JOptionPane.showOptionDialog(frame, "Do you want to save changes or close?", "Save or Close",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case JOptionPane.YES_OPTION:
                showSaveOptionsHistogram();
                break;
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CANCEL_OPTION:
               frame.setVisible(false);
               //frame.dispose();
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
        Object[] saveOptions = {"CSV", "SVG", "PNG"};
        int formatChoice = JOptionPane.showOptionDialog(frame, "Choose the format to save the chart:", "Save Chart Format",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, saveOptions, saveOptions[0]);

        if (formatChoice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String fileExtension = formatChoice == 0 ? ".csv" : formatChoice == 1 ? ".svg" : ".png";
        String defaultFileName = "HistogramChart" + fileExtension;

        int width = 1000, height = 1000;

        if (formatChoice == 1 || formatChoice == 2) {
            String defaultSize = formatChoice == 1 ? "1200x730" : "2400x1560";
            String sizeInput = JOptionPane.showInputDialog(frame, "Enter dimensions (width x height):", defaultSize);
            if (sizeInput == null) {
                return;
            }
            String[] sizes = sizeInput.split("x");
            try {
                width = Integer.parseInt(sizes[0].trim());
                height = Integer.parseInt(sizes[1].trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid dimensions. Using default values.");
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select directory and filename to save");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setSelectedFile(new File(defaultFileName)); // Пропонуємо назву файлу

        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getName().endsWith(fileExtension)) {
                fileToSave = new File(fileToSave.getAbsolutePath() + fileExtension);
            }

            try {
                if (formatChoice == 0) {
                    saveHistogramDataToCSV(fileToSave.getAbsolutePath());
                } else if (formatChoice == 1) {
                    saveHistogramToSVG(fileToSave.getAbsolutePath(), width, height);
                } else if (formatChoice == 2) {
                    saveHistogramToPNG(fileToSave.getAbsolutePath(), width, height);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void saveHistogramToSVG(String filePath, int width, int height) {
        if (frame == null || frame.getContentPane().getComponentCount() == 0) {
            System.err.println("Гістограма ще не була створена або відображена.");
            return;
        }

        SVGGraphics2D g2 = new SVGGraphics2D(width, height);
        ChartPanel chartPanel = (ChartPanel) frame.getContentPane().getComponent(0);
        chartPanel.getChart().draw(g2, new Rectangle(0, 0, width, height));

        try {
            SVGUtils.writeToSVG(new File(filePath), g2.getSVGElement());
            System.out.println("Гістограма успішно збережена у форматі SVG.");
        } catch (IOException e) {
            System.err.println("Помилка при збереженні гістограми у форматі SVG: " + e.getMessage());
        }
    }

    public static void saveHistogramToSVG(String filePath) {
        int width = SimulationGUI.WIDTH_OF_SVG_PICTURE;
        int height = SimulationGUI.HEIGHT_OF_SVG_PICTURE;

        if (frame == null || frame.getContentPane().getComponentCount() == 0) {
            System.err.println("Гістограма ще не була створена або відображена.");
            return;
        }

        SVGGraphics2D g2 = new SVGGraphics2D(width, height);


        ChartPanel chartPanel = (ChartPanel) frame.getContentPane().getComponent(0);

        chartPanel.getChart().draw(g2, new Rectangle(0, 0, width, height));


        try {
            SVGUtils.writeToSVG(new File(filePath), g2.getSVGElement());
            System.out.println("Гістограма успішно збережена у форматі SVG з розмірами 1400x700.");
        } catch (IOException e) {
            System.err.println("Помилка при збереженні гістограми у форматі SVG: " + e.getMessage());
        }
    }

    public static void saveHistogramDataToCSV(String filePath) throws IOException {
        DecimalFormat df = new DecimalFormat("#.######################");
        df.setDecimalSeparatorAlwaysShown(false);

        FileWriter csvWriter = new FileWriter(filePath);
        csvWriter.append("Arrival Rate");

        // Отримання кількості рядів та стовпців даних
        int seriesCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();

        for (int column = 0; column < columnCount; column++) {
            String binRange = dataset.getColumnKey(column).toString();
            csvWriter.append("; ").append(binRange);
        }
        csvWriter.append("\n");
        for (int series = 0; series < seriesCount; series++) {
            double arrivalRate = (series + 1) * simParameters.getARRIVAL_RATE_STEP();
            csvWriter.append(formatDouble(df, arrivalRate));
            for (int column = 0; column < columnCount; column++) {
                Number probability = dataset.getValue(series, column);
                csvWriter.append(";").append(formatDouble(df, probability.doubleValue()));
            }
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    public static void saveHistogramToPNG(String filePath, int width, int height) {
        try {
            if (frame != null && frame.getContentPane().getComponentCount() > 0 && frame.getContentPane().getComponent(0) instanceof ChartPanel) {
                ChartPanel chartPanel = (ChartPanel) frame.getContentPane().getComponent(0);
                JFreeChart chart = chartPanel.getChart();
                ChartUtilities.saveChartAsPNG(new File(filePath), chart, width, height);
                System.out.println("Гістограма успішно збережена у форматі PNG.");
            } else {
                System.err.println("Chart not found or frame is not initialized.");
            }
        } catch (IOException e) {
            System.err.println("Problem occurred while saving the chart to PNG: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveHistogramToPNG(String filePath) {
        try {

            int width = SimulationGUI.WIDTH_OF_PNG_PICTURE;
            int height = SimulationGUI.HEIGHT_OF_PNG_PICTURE;

            if (frame != null && frame.getContentPane().getComponentCount() > 0 && frame.getContentPane().getComponent(0) instanceof ChartPanel) {
                ChartPanel chartPanel = (ChartPanel) frame.getContentPane().getComponent(0);
                JFreeChart chart = chartPanel.getChart();
                ChartUtilities.saveChartAsPNG(new File(filePath), chart, width, height);
            } else {
                System.err.println("Chart not found or frame is not initialized.");
            }
        } catch (IOException e) {
            System.err.println("Problem occurred while saving the chart to PNG.");
            e.printStackTrace();
        }
    }

    static void resetDataHistogram() {
        dataset.clear();
        seriesCounter = 0;
    }
    public static JFrame getHistogramFrame() {
        return frame;
    }

}


/*
    private int histogramCount = 0;
        private JFreeChart sitePowerChart;
    private ChartPanel chartPanel;
    private JFrame chartFrame;
    private boolean isChartInitialized = false;
    private int seriesCount = 0;
    private CombinedDomainXYPlot histogramPlot;

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
                initializeHistogram();
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

                if (histogramPlot.getSubplots().size() == 0) {
                    histogramPlot.add(plot);
                } else {
                    histogramPlot.setDataset(histogramDataset);
                    histogramPlot.setRenderer(renderer);
                }

                chartFrame.validate();
                chartFrame.repaint();
            }

        } else {
            System.out.println("No non-zero data available for histogram.");
        }
    }

    private void initializeHistogram() {
        histogramPlot = new CombinedDomainXYPlot(new NumberAxis("Values"));
        histogramPlot.setGap(10.0);

        sitePowerChart = new JFreeChart("Site Power Distribution Histograms",
                JFreeChart.DEFAULT_TITLE_FONT, histogramPlot, true);

        chartPanel = new ChartPanel(sitePowerChart);
        chartFrame = new JFrame("Histograms");
        chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chartFrame.setSize(800, 600);
        chartFrame.setContentPane(chartPanel);
        chartFrame.setVisible(true);

        isChartInitialized = true;
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
    }*/