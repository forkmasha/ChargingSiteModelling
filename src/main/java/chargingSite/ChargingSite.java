package chargingSite;

import com.orsoncharts.*;
import com.orsoncharts.axis.NumberAxis3D;
import com.orsoncharts.data.xyz.XYZSeries;
import com.orsoncharts.data.xyz.XYZSeriesCollection;
import com.orsoncharts.graphics3d.ViewPoint3D;
import com.orsoncharts.legend.LegendAnchor;
import com.orsoncharts.legend.StandardLegendBuilder;
import com.orsoncharts.plot.XYZPlot;

import com.orsoncharts.util.Orientation;
import eventSimulation.EventSimulation;
import exceptions.SitePowerExceededException;
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
    public static Color lowBLUE = new Color(0, 127, 255);
    public static Color highRED = new Color(255, 0, 127);
    private static Color[] colors; // colour array to be initialised with n = number of simulation steps

    private static void initColors (int n) {
        initColors(n, lowBLUE, highRED);
    }
    private static void initColors(int n, Color ci) {
        colors = new Color[n];
        for (int i = 0; i < n; i++) {
            colors[i] = ci;
        }
    }
    private static void initColors(int n, Color c1, Color c2) {
        colors = new Color[n];
        int R, G, B;
        double p;
        for (int i = 0; i < n; i++) {
            p = (double) i / (n-1);
            R = (int) Math.round((1-p) * c1.getRed() + p * c2.getRed());
            G = (int) Math.round((1-p) * c1.getGreen() + p * c2.getGreen());
            B = (int) Math.round((1-p) * c1.getBlue() + p * c2.getBlue());
            colors[i] = new Color(R,G,B);
        }
    }


    public ChargingSite(SimulationParameters parameters) {
        this.simParameters = parameters;
        this.numberOfChargingPoints = parameters.getNUMBER_OF_SERVERS();
        this.maxSitePower = parameters.getMaxSitePower();
        initializeChargingPoints();
        initColors(parameters.getSIM_STEPS());
    }

    private void initializeChargingPoints() {
        for (int i = 0; i < numberOfChargingPoints; i++) {
            chargingPowers.add((double) simParameters.MAX_POINT_POWER);
            chargingPoints.add(new ChargingPoint(simParameters.MAX_POINT_POWER));
        }
    }

    public void resetPowerRecords() {
        sitePowerSeries.clear();
        sitePower1.clear();
        dataList.clear();
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
    public void executeClockTick(double timeScale) {
        //do something -> record system states...
        double power = getSitePower();
        // sitePowerSeries.add(EventSimulation.getCurrentTime(), sitePower);
        //if (!isFirstValue && (EventSimulation.getCurrentTime() != previousTime || power != previousSitePower)) {
        //dataList.add(new TimePowerData(EventSimulation.getCurrentTime(), power));
        dataList.add(new TimePowerData(EventSimulation.getCurrentTime()*timeScale, power));
        addSitePower(power);
        //}
        //previousTime = EventSimulation.getCurrentTime();
        //previousSitePower = power;
        //isFirstValue = false;
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
                "Charging site Power over Time",
                "Time [h]",
                "Power [kW]",
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

        int frameWidth = (int) (largestBounds.width * 0.332);
        int frameHeight = (int) (largestBounds.height * 0.473);

        int offsetX = (int) (largestBounds.width * 0.015);

        frame1.setSize(frameWidth, frameHeight);
        frame1.setLocation(largestBounds.x + largestBounds.width - frameWidth - offsetX, largestBounds.y);

        frame1.setVisible(true);
    }

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
        if (colors == null) initColors(parameters.getSIM_STEPS());

        double maxTime = 1 + 25 * parameters.getMaxArrivalRate() / parameters.getMaxEvents();
        double arrivalRate = (dataset1.getSeriesCount() + 1) * parameters.getMaxArrivalRate() / parameters.getSimSteps();

        XYSeries series = new XYSeries(String.format("%.1f EV/h", arrivalRate));
        for (TimePowerData data : dataList) {
            if (data.getTime() > maxTime && data.getTime() <= 2 * maxTime) {
                series.add(data.getTime(), data.getPower());
            }
        }

        /* double progress = (double) dataset1.getSeriesCount() / parameters.getSIM_STEPS();
        int R = 0;
        int G = (int) Math.floor(255 * progress);
        int B = 255;
        */
        Shape cross = ShapeUtilities.createDiagonalCross(2.1f, 0.15f); //.createRegularCross(1, 1);.createDiamond(2.1f);

        dataset1.addSeries(series);
        XYPlot plot = (XYPlot) chartPanel1.getChart().getPlot();

        //plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD); //would be nice but colouring malfunctions with that option
        plot.setSeriesRenderingOrder(SeriesRenderingOrder.REVERSE);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesLinesVisible(dataset1.getSeriesCount() - 1, false);
        renderer.setSeriesShapesVisible(dataset1.getSeriesCount() - 1, true);
        renderer.setSeriesShape(dataset1.getSeriesCount() - 1, cross);
        //plot.getRenderer().setSeriesPaint(dataset1.getSeriesCount() - 1, new Color(R, G, B));
        plot.getRenderer().setSeriesPaint(dataset1.getSeriesCount() - 1, colors[dataset1.getSeriesCount() - 1]);

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
   private static JPanel mainPanel;
   public static void initializeHistogramFrame() {
       frame = new JFrame("Charging site Power Histogram");
       frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
       frame.addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e) {
               promptSaveOnCloseHistogram();
           }
       });
       mainPanel = new JPanel(new BorderLayout());
       frame.setContentPane(mainPanel);

       JButton toggleLegendButton = new JButton("Toggle Legend");
       toggleLegendButton.addActionListener(e -> {

           ChartPanel chartPanel = (ChartPanel) mainPanel.getComponent(1);
           JFreeChart chart = chartPanel.getChart();
           chart.getLegend().setVisible(!chart.getLegend().isVisible());
           chartPanel.repaint();
       });
       mainPanel.add(toggleLegendButton, BorderLayout.SOUTH);

       frame.setSize(getPreferredFrameSize());
       frame.setLocation(getPreferredFrameLocation());
       frame.setVisible(true);
   }

   public static void plotHistogram(ArrayList<Double> data, int numBins, SimulationParameters parameters) {
       if (frame == null) {
           initializeHistogramFrame();
       }
       updateDataset(data, numBins, parameters);
       JFreeChart chart = createHistogramChart();

       ChartPanel chartPanel = new ChartPanel(chart);
       configureChartPanel(chartPanel);

       if (mainPanel.getComponentCount() > 1) {
           mainPanel.remove(1);
       }
       mainPanel.add(chartPanel, BorderLayout.CENTER);
       mainPanel.revalidate();
       mainPanel.repaint();
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
            String label = String.format("%.1f EV/h", parameters.getARRIVAL_RATE_STEP() * (1 + seriesCounter));
            dataset.addValue((double) bins[i] / data.size(),
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
                dataset,
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
        chartPanel.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
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
            if ((largestBounds.isEmpty()) || (bounds.getWidth() * bounds.getHeight() > largestBounds.getWidth() * largestBounds.getHeight())) {
                largestBounds = bounds;
            }
        }
        Dimension frameSize = getPreferredFrameSize();
        int frameX = (int) (largestBounds.x + largestBounds.width - frameSize.width - (largestBounds.width * 0.016));

        int frameY = (int) (largestBounds.y + (largestBounds.height - frameSize.height) * 0.5 + largestBounds.height * 0.205);

        return new Point(frameX, frameY);
    }

    static JFrame frame2 = new JFrame("Charging site Power Histogram");
    private static Chart3D chart;
    private static boolean legendVisible = false;
    private static XYZSeriesCollection<String> dataset2 = new XYZSeriesCollection<>();

    public static void plotHistogram3D(double arrivalRate, ArrayList<Double> data, int numBins, SimulationParameters parameters) {
        double min = 0;
        double max = parameters.MAX_SITE_POWER;
        double binWidth = (max - min) / numBins;
        if (colors == null) {
            //initColors(parameters.getSIM_STEPS(),Color.BLACK);
            initColors(parameters.getSIM_STEPS(), new Color(0, 0, 255), new Color(255, 0, 127));
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
        dataset2.add(series);

        NumberAxis3D xAxis = new NumberAxis3D("X-axis");
        NumberAxis3D yAxis = new NumberAxis3D("Y-axis");
        NumberAxis3D zAxis = new NumberAxis3D("Z-axis");

        //Chart3D chart = Chart3DFactory.createXYZLineChart("XYZ Chart", "Chart description", dataset2, "Site Power", "Arrival Rate", "Probability Mass");
       chart = Chart3DFactory.createXYZLineChart("Charging site Power Histogram", "", dataset2, "Site Power [kW]", "Arrival Rate [EV/h]", "Probability Mass");
        updateLegendVisibility();
        chart.setLegendOrientation(Orientation.VERTICAL);
        chart.setLegendAnchor(LegendAnchor.TOP_RIGHT);

        XYZPlot plot3D = (XYZPlot) chart.getPlot();
        chart.setChartBoxColor(Color.white);
        plot3D.getRenderer().setColors(colors);
        chart.setLegendBuilder(null);
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        chart.setTitle("Charging site power Histogram",titleFont,Color.black);
        chart.setTitleAnchor(TitleAnchor.TOP_CENTER);

        // CategoryPlot3D plot =  (CategoryPlot3D)chart.getPlot();
        //XYZPlot plot =new XYZPlot (dataset2, renderer, xAxis, yAxis, zAxis);
        //--  XYZPlot plot =(XYZPlot)chart.getPlot();
        //-- plot.getRenderer().setColors(Colors.getColors2());
        //  plot.getRenderer().setColors(Colors.createFancyDarkColors());
        //  plot.setRenderer(renderer);
        //  renderer.getColorSource().getColor(1,1);

        Chart3DPanel chartPanel = new Chart3DPanel(chart);
        ViewPoint3D viewPoint = new ViewPoint3D(-0.775, -1.425, calculateOptimalViewAngleForLargestScreen(), 0); //-0.775, -1.425, 35, 0); //45/70
        chartPanel.setViewPoint(viewPoint);

        if (frame2.getContentPane().getComponentCount() > 0) {
            frame2.getContentPane().removeAll();
        };

        frame2.add(chartPanel);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame2.setSize(1200, 720);
        adjustFrameToScreen();
        frame2.validate();
        addLegendToggle();
        frame2.repaint();
        frame2.setVisible(true);
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
        double screenDiagonal = Math.sqrt(maxScreenSize.width * maxScreenSize.width + maxScreenSize.height * maxScreenSize.height) / Toolkit.getDefaultToolkit().getScreenResolution();
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
            frame2.setSize(frameWidth, frameHeight);

            int xPosition = largestBounds.x + largestBounds.width - frameWidth;
            int yPosition = largestBounds.y + largestBounds.height - frameHeight;

            yPosition -= frameHeight * 0.13;
            xPosition -= frameWidth * 0.05;

            frame2.setLocation(xPosition, yPosition);
        }
    }
    private static void addLegendToggle() {
        JToggleButton toggleButton = new JToggleButton("Toggle Legend");
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legendVisible = !legendVisible;
                updateLegendVisibility();
                frame2.repaint();
            }
        });

        frame2.getContentPane().add(toggleButton, BorderLayout.SOUTH); // Додавання кнопки в нижню частину вікна
    }

    private static void updateLegendVisibility() {
        if (chart != null) {
            if (legendVisible) {
                StandardLegendBuilder legendBuilder = new StandardLegendBuilder();
                chart.setLegendBuilder(legendBuilder);
            } else {
                chart.setLegendBuilder(null);
            }
        }
    }
    static void resetData3DHistogram() {
        dataset2.removeAll();
        seriesCounter = 0;
    }


    public static void saveHistogramData3DToCSV(String filePath) throws IOException {
        DecimalFormat df = new DecimalFormat("#.######################");
        df.setDecimalSeparatorAlwaysShown(false);

        try (FileWriter csvWriter = new FileWriter(filePath)) {
            csvWriter.append("Arrival Rate");

            int seriesCount = dataset2.getSeriesCount();

            int maxItemCount = 0;
            for (int i = 0; i < seriesCount; i++) {
                int itemCount = dataset2.getSeries(i).getItemCount();
                if (itemCount > maxItemCount) {
                    maxItemCount = itemCount;
                }
            }
                //first line of csv file
            for (int column = 0; column < maxItemCount; column++) {
                //  csvWriter.append("; ").append("Bin ").append(Integer.toString(column + 1));
                double binWidth = simParameters.MAX_SITE_POWER / maxItemCount;
                String binRange = column * binWidth + "-" + (column + 1) * binWidth;
                //  String binRange = dataset.getColumnKey(column).toString();
                csvWriter.append("; ").append(binRange);
            }
            csvWriter.append("\n");

                //following lines of csv file
            for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                XYZSeries<String> series = dataset2.getSeries(seriesIndex);
                csvWriter.append(series.getKey());

                for (int itemIndex = 0; itemIndex < series.getItemCount(); itemIndex++) {
                    double zValue = series.getZValue(itemIndex);

                    csvWriter.append(";").append(formatDouble(df, zValue));
                }
                csvWriter.append("\n");
            }
            csvWriter.flush();
        }
    }


    public static void saveHistogram3DToPNG(String filePath) {
        if (frame2.getContentPane().getComponentCount() > 0 && frame2.getContentPane().getComponent(0) instanceof Chart3DPanel) {
            Chart3DPanel chartPanel = (Chart3DPanel) frame2.getContentPane().getComponent(0);
            int originalWidth = chartPanel.getWidth();
            int originalHeight = chartPanel.getHeight();

            int width = originalWidth * 3;
            int height = originalHeight * 3;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            g2.scale(3.0, 3.0);

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
        if (frame2.getContentPane().getComponentCount() > 0 && frame2.getContentPane().getComponent(0) instanceof Chart3DPanel) {
            Chart3DPanel chartPanel = (Chart3DPanel) frame2.getContentPane().getComponent(0);

            // Використання заданих параметрів ширини та висоти для створення зображення
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            // Масштабування зображення залежно від заданих параметрів ширини та висоти
            double scaleX = (double) width / chartPanel.getWidth();
            double scaleY = (double) height / chartPanel.getHeight();
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

            for (int seriesIndex = 0; seriesIndex < dataset2.getSeriesCount(); seriesIndex++) {
                XYZSeries<String> series = dataset2.getSeries(seriesIndex);

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

    public static void saveHistogram3DToSVG(String filePath) {
        if (frame2.getContentPane().getComponentCount() > 0 && frame2.getContentPane().getComponent(0) instanceof Chart3DPanel) {
            Chart3DPanel chartPanel = (Chart3DPanel) frame2.getContentPane().getComponent(0);

            int width = 1200;
            int height = 720;

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

    public static void saveHistogram3DToSVG(String filePath, int width, int height) {
        if (frame2.getContentPane().getComponentCount() > 0 && frame2.getContentPane().getComponent(0) instanceof Chart3DPanel) {
            Chart3DPanel chartPanel = (Chart3DPanel) frame2.getContentPane().getComponent(0);

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
        if (frame == null) {
            System.err.println("Histogram frame not initialized");
            return;
        }

        if (mainPanel.getComponentCount() > 1) {
            ChartPanel chartPanel = (ChartPanel) mainPanel.getComponent(1);
            JFreeChart chart = chartPanel.getChart();


            SVGGraphics2D g2 = new SVGGraphics2D(width, height);
            Rectangle2D chartArea = new Rectangle2D.Double(0, 0, width, height);


            chart.draw(g2, chartArea);


            File svgFile = new File(filePath);
            try {
                SVGUtils.writeToSVG(svgFile, g2.getSVGElement());
                System.out.println("Histogram saved to SVG successfully.");
            } catch (IOException e) {
                System.err.println("Error saving histogram to SVG: " + e.getMessage());
            }
        } else {
            System.err.println("ChartPanel not found in mainPanel");
        }
    }
    public static void saveHistogramToSVG(String filePath) {

        if (frame == null) {
            System.err.println("Histogram frame not initialized");
            return;
        }
        if (mainPanel.getComponentCount() > 1) {
            ChartPanel chartPanel = (ChartPanel) mainPanel.getComponent(1);
            JFreeChart chart = chartPanel.getChart();

            SVGGraphics2D g2 = new SVGGraphics2D(SimulationGUI.WIDTH_OF_SVG_PICTURE, SimulationGUI.HEIGHT_OF_SVG_PICTURE);
            Rectangle2D chartArea = new Rectangle2D.Double(0, 0, SimulationGUI.WIDTH_OF_SVG_PICTURE, SimulationGUI.HEIGHT_OF_SVG_PICTURE);

            chart.draw(g2, chartArea);

            File svgFile = new File(filePath);
            try {
                SVGUtils.writeToSVG(svgFile, g2.getSVGElement());
                System.out.println("Histogram saved to SVG successfully.");
            } catch (IOException e) {
                System.err.println("Error saving histogram to SVG: " + e.getMessage());
            }
        } else {
            System.err.println("ChartPanel not found in mainPanel");
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
        if (frame == null) {
            System.err.println("Histogram frame not initialized");
            return;
        }
        Component[] components = frame.getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof ChartPanel) {
                ChartPanel chartPanel = (ChartPanel) comp;
                JFreeChart chart = chartPanel.getChart();

                try {
                    ChartUtilities.saveChartAsPNG(new File(filePath), chart, width, height);
                    System.out.println("Histogram saved to PNG successfully.");
                    return;
                } catch (IOException e) {
                    System.err.println("Error saving histogram to PNG: " + e.getMessage());
                    return;
                }
            }
        }
        System.err.println("ChartPanel not found");
    }

    public static void saveHistogramToPNG(String filePath) {
        if (frame == null) {
            System.err.println("Histogram frame not initialized");
            return;
        }

        if (mainPanel.getComponentCount() > 1) {
            ChartPanel chartPanel = (ChartPanel) mainPanel.getComponent(1);
            JFreeChart chart = chartPanel.getChart();

            int width = chartPanel.getWidth();
            int height = chartPanel.getHeight();

            try {
                ChartUtilities.saveChartAsPNG(new File(filePath), chart, SimulationGUI.WIDTH_OF_PNG_PICTURE, SimulationGUI.HEIGHT_OF_PNG_PICTURE);
                System.out.println("Histogram saved to PNG successfully.");
            } catch (IOException e) {
                System.err.println("Error saving histogram to PNG: " + e.getMessage());
            }
        } else {
            System.err.println("ChartPanel not found in mainPanel");
        }
    }

    static void resetDataHistogram() {
        dataset.clear();
        seriesCounter = 0;
    }

    static void reset3DDataHistogram() {
        dataset2.removeAll();
        seriesCounter = 0;

    }
    public static JFrame getHistogramFrame() {
        return frame;
    }

    public static JFrame get3dHistogramFrame() {
        return frame2;
    }
}