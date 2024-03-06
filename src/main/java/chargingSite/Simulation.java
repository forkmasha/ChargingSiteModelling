package chargingSite;

import distributions.Distribution;
import eventSimulation.*;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import queueingSystem.Client;
import queueingSystem.QueueingSystem;
import results.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static chargingSite.ChargingSite.*;
import static org.jfree.chart.ChartFactory.createXYLineChart;

public class Simulation extends Graph {
    /*   private static final double MIN_ARRIVAL_RATE = 0.5;
    private static final double MAX_ARRIVAL_RATE = 25.0;
    private static final double ARRIVAL_RATE_STEP = 0.5;
    private static final int SIM_STEPS = (int) Math.ceil((MAX_ARRIVAL_RATE - MIN_ARRIVAL_RATE) / ARRIVAL_RATE_STEP);
    private static final int NUMBER_OF_CLIENT_TYPES = 1;
    private static final int MAX_EVENTS = 2500;
    private static final int NUMBER_OF_SERVERS = 5;
    private static final int QUEUE_SIZE = 10;
    private static final QueueingType QUEUEING_TYPE = QueueingType.FIFO;
    private static final double MEAN_SERVICE_TIME = 0.5;
    private static final DistributionType ARRIVAL_TYPE = DistributionType.EXPONENTIAL; // EXPONENTIAL is common
    private static final DistributionType SERVICE_TYPE = DistributionType.ERLANGD;   // ERLANGD is a good choice
    private static int confLevel = 98;*/
    private static final Logger LOGGER = Logger.getLogger(Simulation.class.getName());
    private static JFreeChart MyChart;
    private JFreeChart SitePowerGraph;
    public Monitor chargingMonitor;
    public ChargingSite site;
    private final Times meanServiceTimes = new Times("ArrivalRate", "MeanServiceTime");
    private final Times meanQueueingTimes = new Times("ArrivalRate", "MeanQueueingTime");
    private final Times meanSystemTimes = new Times("ArrivalRate", "MeanSystemTime");
    private final XYSeries analyticWaitingTimes = new XYSeries("Value");

    //----------------private Monitor meanEnergyCharged = new Monitor();// collect mean, std, confidence

    private SimulationParameters parameters = new SimulationParameters();

    public SimulationParameters getParameters() {
        return parameters;
    }

    public void setConfLevel(int confLevel) {
        parameters.setConfLevel(confLevel);
        this.chargingMonitor = new Monitor(confLevel);
    }

    public Simulation() {
        calcAvgServiceTime();
    }

    private double calcAvgServiceTime() {
        return parameters.getAvgServiceTime();
    }

    public double calcMMnNwaitingTime(double rho) {
        return parameters.getMMnNwaitingTime(rho);
    }

    public void saveQueueingCharacteristicsAsSVG(int wi, int hi, File svgFile) throws IOException {

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        MyChart.draw(svgGenerator, new Rectangle2D.Double(0, 0, wi, hi));

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(svgFile), StandardCharsets.UTF_8)) {
            svgGenerator.stream(writer, true);
        }
    }

    public String getKendallName() {
        return parameters.getKendallName();
    }

    public void runSimulation() {
        resetDataHistogram();
        ChargingSite.clearPowerOverTimeDataset1();
        EventSimulation.setMaxEvents(parameters.getMAX_EVENTS());
        Client[] myFirstClients = new Client[1];
        Client myFirstClient;
        QueueingSystem mySystem = new QueueingSystem(parameters);
        site = mySystem.getChargingSite();
        chargingMonitor.setSource(mySystem);
        if (parameters.getNUMBER_OF_CAR_TYPES() > 1) {
            mySystem.setName(Distribution.getTitleAbbreviation(parameters.getARRIVAL_TYPE().toString())
                    + "/MIXED/"
                    + parameters.getNUMBER_OF_SERVERS() + "/" + (parameters.getNUMBER_OF_SERVERS() + parameters.getQUEUE_SIZE()));
        } else {
            mySystem.setName(Distribution.getTitleAbbreviation(parameters.getARRIVAL_TYPE().toString())
                    + "/" + Distribution.getTitleAbbreviation(parameters.getSERVICE_TYPE().toString())
                    + "/" + parameters.getNUMBER_OF_SERVERS() + "/" + (parameters.getNUMBER_OF_SERVERS() + parameters.getQUEUE_SIZE()));
        }
        mySystem.setDistributionType(parameters.getARRIVAL_TYPE());
        int stepCounter = 0;
        double arrivalRate = parameters.getMIN_ARRIVAL_RATE();
        List<Double> dummy = new ArrayList<>();

        //for (double arrivalRate = MIN_ARRIVAL_RATE; arrivalRate <= MAX_ARRIVAL_RATE; arrivalRate += ARRIVAL_RATE_STEP) {
        while (stepCounter < parameters.getSIM_STEPS()) {
            if (arrivalRate > parameters.getMAX_ARRIVAL_RATE() + 0.001)
                System.out.println("WARNING: Arrival rate " + arrivalRate + " beyond maximum " + parameters.getMAX_ARRIVAL_RATE() + " occurred!");
            stepCounter++;
            mySystem.resetQueueingSystem();
            mySystem.setMeanInterArrivalTime(myFirstClients.length / arrivalRate); //mean inter-arrival time per client

            // add as manny client types as necessary -> adjust the numberOfClientTypes accordingly!
        /*    if (NUMBER_OF_CLIENT_TYPES>2) {
                myFirstClients[1] = new Client(0.0, PERCENTAGE_OF_CARS_3*MEAN_SERVICE_TIME_3, SERVICE_TYPE_3, mySystem);  // set service time per client
            }
            if (NUMBER_OF_CLIENT_TYPES>1) {
                myFirstClients[1] = new Client(0.0, PERCENTAGE_OF_CARS_2*MEAN_SERVICE_TIME_2, SERVICE_TYPE_2, mySystem);  // set service time per client
            }
        */
            //for (int i=0; i < myFirstClients.length; i++) {
            myFirstClients[0] = new Client(0.0, ElectricVehicle.createRandomCar(parameters), mySystem);
            //myFirstClients[i] = new Client(0.0, AVERAGE_SERVICE_TIME, SERVICE_TYPE[i], mySystem);  // set service time per client
            //}
            EventSimulation.run(myFirstClients);

            // add the site powers of the current run to the existing graph (if not possible draw one and ask what to do (save and continue / discard and continue))
            //  mySystem.getChargingSite().addSitePowerGraph(SitePowerGraph);
            // mySystem.getChargingSite().addSitePowerGraph();
            // mySystem.getChargingSite().addSitePowerHistogram();

            // XYSeries series = mySystem.getChargingSite().getSitePowerSeries();
            // mySystem.getChargingSite().addSitePower3DHistogram(series, arrivalRate);


            // Histogram.generateHistogram(25, mySystem.getSitePowers(), null,"Site Power Histogram @ " + stepCounter);
            plotHistogram3D(stepCounter*parameters.getARRIVAL_RATE_STEP() , mySystem.getChargingSite().getSitePower1(), 20, parameters);
            plotHistogram(mySystem.getChargingSite().getSitePower1(), 20, parameters);


            mySystem.getChargingSite().displayPowerOverTimeChart(dataList, parameters);



            // mySystem.getChargingSite().visualizeSitePower();
            //  mySystem.getChargingSite().displayChart();

            //   ChartGenerator.initializeChart();
            //  ChartGenerator.displayChart(dataList, ChartGenerator.frame);


            //dummy.add(this.calcMMnNwaitingTime(arrivalRate * this.MEAN_SERVICE_TIME / this.NUMBER_OF_SERVERS));
            analyticWaitingTimes.add(arrivalRate,
                    this.calcMMnNwaitingTime(arrivalRate * calcAvgServiceTime() / parameters.getNUMBER_OF_SERVERS()));

            meanServiceTimes.addStep(arrivalRate);
            meanServiceTimes.addMean(mySystem.getTimesInService());
            meanServiceTimes.addStds(mySystem.getTimesInService());
            meanServiceTimes.addConfidence(mySystem.getTimesInService(), parameters.getConfLevel());

            meanQueueingTimes.addStep(arrivalRate);
            meanQueueingTimes.addMean(mySystem.getTimesInQueue());
            meanQueueingTimes.addStds(mySystem.getTimesInQueue());
            meanQueueingTimes.addConfidence(mySystem.getTimesInQueue(), parameters.getConfLevel());

            meanSystemTimes.addStep(arrivalRate);
            meanSystemTimes.addMean(mySystem.getTimesInSystem());
            meanSystemTimes.addStds(mySystem.getTimesInSystem());
            meanSystemTimes.addConfidence(mySystem.getTimesInSystem(), parameters.getConfLevel());

            chargingMonitor.storeStep(arrivalRate);
            chargingMonitor.storeMean();
            chargingMonitor.storeStd();
            chargingMonitor.storeMax();
            chargingMonitor.storeMin();
            chargingMonitor.storeConf();
            chargingMonitor.store90thQuantile();
            chargingMonitor.store10thQuantile();

            chargingMonitor.storeMeanSitePower();
            chargingMonitor.storeMaxSitePower();
            chargingMonitor.storeStdSitePower();
            chargingMonitor.storeConfSitePower();

            chargingMonitor.storeMeanCD();
            chargingMonitor.storeStdCD();
            chargingMonitor.storeConfCD();

            Statistics calc = new Statistics();

            LOGGER.info(">--------- " + mySystem.getSystemName() + " Simulation step# " + stepCounter + " done -----------<"
                    + "\n Mean Inter Arrival Time: " + 1.0 / arrivalRate
                    + "\n Service Time (" + mySystem.getTimesInService().size() + "): "
                    + calc.getMean(mySystem.getTimesInService()) + "/"
                    + calc.getStd(mySystem.getTimesInService()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInService(), parameters.getConfLevel())
                    + "\n Queueing Time (" + mySystem.getTimesInQueue().size() + "): "
                    + calc.getMean(mySystem.getTimesInQueue()) + "/"
                    + calc.getStd(mySystem.getTimesInQueue()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInQueue(), parameters.getConfLevel())
                    + "\n System Time (" + mySystem.getTimesInSystem().size() + "): "
                    + calc.getMean(mySystem.getTimesInSystem()) + "/"
                    + calc.getStd(mySystem.getTimesInSystem()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInSystem(), parameters.getConfLevel())
                    + "\n Charged Energy (" + mySystem.getAmountsCharged().size() + "): "
                    + calc.getMean(mySystem.getAmountsCharged()) + "/"
                    + calc.getStd(mySystem.getAmountsCharged()) + "/"
                    + calc.getConfidenceInterval(mySystem.getAmountsCharged(), parameters.getConfLevel())
                    + "\n Site Power Demand (" + mySystem.getSitePowers().size() + "): "
                    + calc.getMean(mySystem.getSitePowers()) + "/"
                    + calc.getStd(mySystem.getSitePowers()) + "/"
                    + calc.getConfidenceInterval(mySystem.getSitePowers(), parameters.getConfLevel())
                    + "\n Queue state: " + mySystem.getMyQueue().getOccupation()
                    + " Server state: " + mySystem.getNumberOfServersInUse()
                    + " Clients done: " + Client.getClientCounter()
            );

            arrivalRate += parameters.getARRIVAL_RATE_STEP();
        }
        drawGraphQueueingCharacteristics();

        chargingMonitor.drawGraphEnergyCharacteristics(this);


        //plotHistogram(mySystem.getChargingSite().getSitePower1(), 15);
    }
    public static JFrame queueingCharacteristicsFrame;
    private static final List<JFrame> openWindows = new ArrayList<>();

    public static void addWindow(JFrame frame) {
        openWindows.add(frame);
    }

    public static void closeAllWindows() {
        for (JFrame frame : openWindows) {
            frame.dispose();
        }
        openWindows.clear();
    }


    public void drawGraphQueueingCharacteristics() {   // D/D/5/10 Queueing System

        String title = "Charging Site Queueing Characteristics \n"
                + this.getKendallName() + " Queueing System"
                + " (" + parameters.getMAX_EVENTS() + " samples per evaluation point)";

        String[] titleParts = title.split("\n");

        TextTitle textTitle = new TextTitle(titleParts[0]);
        textTitle.setFont(new Font("Arial", Font.BOLD, 24));

        TextTitle textSubtitle = new TextTitle(titleParts[1]);
        textSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        XYSeriesCollection dataset = new XYSeriesCollection();

        meanSystemTimes.addGraphs(dataset);
        meanServiceTimes.addGraphs(dataset);
        meanQueueingTimes.addGraphs(dataset);
        //analyticWaitingTimes.addGraphs(dataset);
        dataset.addSeries(analyticWaitingTimes);

        MyChart = createXYLineChart(
                "",
                "Arrival Rate [1/h]",
                "Mean and Std [h]",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        MyChart.addSubtitle(textTitle);
        MyChart.addSubtitle(textSubtitle);

        XYPlot plot = MyChart.getXYPlot();
        NumberAxis x_Axis = (NumberAxis) plot.getDomainAxis();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        if (parameters.getAVERAGE_SERVICE_TIME() > 0) {
            yAxis.setRange(0, Math.ceil(1.1 * parameters.getAVERAGE_SERVICE_TIME() * (1 + parameters.getQUEUE_SIZE() / parameters.getNUMBER_OF_SERVERS())));
        } else {
            yAxis.setRange(0, 3);
        }

        int i = 0;
        while (i < parameters.getSIM_STEPS()) {
            renderer.setSeriesPaint(i, Color.MAGENTA);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.magenta);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.magenta);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));


        while (i < 2 * parameters.getSIM_STEPS() + 2) {
            renderer.setSeriesPaint(i, Color.blue);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.blue);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.blue);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));


        while (i < 3 * parameters.getSIM_STEPS() + 4) {
            renderer.setSeriesPaint(i, Color.red);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.red);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.red);
        renderer.setSeriesShape(i, ShapeUtilities.createDiamond(0.75f));
        plot.setRenderer(renderer);

        // draw analytic calculated waiting time of M/M/n/N queueing system
        renderer.setSeriesPaint(++i, Color.black);
        renderer.setSeriesStroke(i, new BasicStroke(0.6f));
        renderer.setSeriesShape(i, ShapeUtilities.createDiagonalCross(0.75f, 0.75f));
        plot.setRenderer(renderer);

        // Add legend
        LegendItemSource legendItemSource = new LegendItemSource() {
            @Override
            public LegendItemCollection getLegendItems() {
                LegendItemCollection items = new LegendItemCollection();

                items.add(new LegendItem("Service Time", Color.blue));
                items.add(new LegendItem("Queuing Time", Color.RED));
                items.add(new LegendItem("System Time", Color.MAGENTA));
                items.add(new LegendItem("M/M/n/N Queueing Time", Color.BLACK));

                return items;
            }
        };

        plot.setFixedLegendItems(legendItemSource.getLegendItems());

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        GraphicsDevice largestScreen = gs[0];
        Rectangle largestBounds = largestScreen.getDefaultConfiguration().getBounds();
        for (GraphicsDevice gd : gs) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            if (bounds.width * bounds.height > largestBounds.width * largestBounds.height) {
                largestScreen = gd;
                largestBounds = bounds;
            }
        }

        int leftOffset = (int) (largestBounds.width * 0.25);
        int windowWidth = (int) (largestBounds.width * 0.4);
        int windowHeight = (int) (largestBounds.height * 0.44);

        ChartPanel chartPanel = new ChartPanel(MyChart);
        chartPanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);

        JFrame frame = new JFrame("Charging Site Queueing Characteristics");
        queueingCharacteristicsFrame = frame;
        Simulation.addWindow(frame);

       /* frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "Do you want to save the figure?", "Save figure before closing", JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    saveSVGDialogue();
                    frame.dispose();
                } else if (result == JOptionPane.NO_OPTION) {
                    frame.dispose();
                }
            }
        });*/


        frame.setContentPane(chartPanel);
        frame.pack();

        frame.setLocation(largestBounds.x + leftOffset, largestBounds.y);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                promptSaveOnCloseQueueingCharacteristics(frame);
            }
        });


        frame.setVisible(true);
    }

    private void promptSaveOnCloseQueueingCharacteristics(JFrame frame) {
        Object[] options = {"Save", "Cancel", "Close"};
        int choice = JOptionPane.showOptionDialog(frame, "Do you want to save the chart before closing?", "Save or Close",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case JOptionPane.YES_OPTION:
                showSaveOptionsQueueingCharacteristics(frame);
                break;
            case JOptionPane.CANCEL_OPTION:
                frame.dispose();
                break;
            case JOptionPane.NO_OPTION:
            default:
                break;
        }
    }
    private void showSaveOptionsQueueingCharacteristics(JFrame frame) {
        Object[] saveOptions = {"CSV", "SVG", "PNG"};
        int formatChoice = JOptionPane.showOptionDialog(frame, "Choose the format to save the chart:", "Save Chart Format",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, saveOptions, saveOptions[0]);

        if (formatChoice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String fileExtension = formatChoice == 0 ? ".csv" : formatChoice == 1 ? ".svg" : ".png";
        String defaultFileName = "ChargingSiteQueueingCharacteristics" + fileExtension;


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
        fileChooser.setSelectedFile(new File(defaultFileName));

        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(fileExtension)) {
                fileToSave = new File(fileToSave.getAbsolutePath() + fileExtension);
            }

            try {
                if (fileExtension.equals(".svg")) {
                    saveQueueingCharacteristicsAsSVG(fileToSave.getAbsolutePath(), width, height);
                } else if (fileExtension.equals(".png")) {
                    saveQueueingCharacteristicsGraphToPNG(fileToSave.getAbsolutePath(), width, height);
                } else if (fileExtension.equals(".csv")) {
                    saveQueueingCharacteristicsToCSV(fileToSave.getAbsolutePath());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void saveQueueingCharacteristicsAsSVG(String filePath, int width, int height) throws IOException {
        File svgFile = new File(filePath);
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(new Dimension(width, height));
        MyChart.draw(svgGenerator, new Rectangle2D.Double(0, 0, width, height));

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(svgFile), StandardCharsets.UTF_8)) {
            svgGenerator.stream(writer, true);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart SVG: " + e.getMessage());
        }
    }
    public static void saveQueueingCharacteristicsGraphToPNG(String filePath, int width, int height) {
        try {
            File PNGFile = new File(filePath);
            ChartUtilities.saveChartAsPNG(PNGFile, MyChart, width, height);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart PNG: " + e.getMessage());
        }
    }
    public void saveQueueingCharacteristicsGraphToPNG(String filePath) {
        try {
            int width = SimulationGUI.WIDTH_OF_PNG_PICTURE;
            int height = SimulationGUI.HEIGHT_OF_PNG_PICTURE;
            File PNGFile = new File(filePath);
            ChartUtilities.saveChartAsPNG(PNGFile, MyChart, width, height);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart PNG.");
        }
    }
    public void saveQueueingCharacteristicsToCSV(String filePath) {
        DecimalFormat df = new DecimalFormat("#.####################");
        df.setDecimalSeparatorAlwaysShown(false);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Arrival Rate [1/h];Mean Service Time;std serviceTime;confidence Service Time; Mean Queueing Time;std queueingTime;confidence Queueing Time; Mean System Time;std systemTime;confidence System Time\n");
            List<Double> arrivalRates = meanSystemTimes.getSteps();
            List<Double> meanServiceTimeValues = meanServiceTimes.getMeans();
            List<Double> meanQueueingTimeValues = meanQueueingTimes.getMeans();
            List<Double> meanSystemTimeValues = meanSystemTimes.getMeans();

            List<Double> stdServiceTimeValues = meanServiceTimes.getStds();
            List<Double> stdQueueingTimeValues = meanQueueingTimes.getStds();
            List<Double> stdSystemTimeValues = meanSystemTimes.getStds();

            List<Double> confServiceTimeValues = meanServiceTimes.getConfidences();
            List<Double> confQueueingTimeValues = meanQueueingTimes.getConfidences();
            List<Double> confSystemTimeValues = meanSystemTimes.getConfidences();

            for (int i = 0; i < arrivalRates.size(); i++) {
                writer.append(formatDouble(df, arrivalRates.get(i))).append(";");
                writer.append(formatDouble(df, meanServiceTimeValues.get(i))).append(";");
                writer.append(formatDouble(df, stdServiceTimeValues.get(i))).append(";");
                writer.append(formatDouble(df, confServiceTimeValues.get(i))).append(";");
                writer.append(formatDouble(df, meanQueueingTimeValues.get(i))).append(";");
                writer.append(formatDouble(df, stdQueueingTimeValues.get(i))).append(";");
                writer.append(formatDouble(df, confQueueingTimeValues.get(i))).append(";");
                writer.append(formatDouble(df, meanSystemTimeValues.get(i))).append(";");
                writer.append(formatDouble(df, stdSystemTimeValues.get(i))).append(";");
                writer.append(formatDouble(df, confSystemTimeValues.get(i))).append("\n");
            }
            System.out.println("CSV file has been created successfully!");

        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }
    private String formatDouble(DecimalFormat df, Double value) {
        return df.format(value);
    }
    public void saveSVGDialogue() {
        Object[] options = {"SVG", "CSV","PNG"};
        int formatResult = JOptionPane.showOptionDialog(
                null,
                "Choose the file format to save:",
                "Choose File Format",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (formatResult == JOptionPane.YES_OPTION) {

            boolean inputValid = getUserInput() && chooseFile();
            if (inputValid) {
                try {
                    int imageWidth = Integer.parseInt(getWidthField().getText());
                    int imageHeight = Integer.parseInt(getHeightField().getText());
                    saveQueueingCharacteristicsAsSVG(imageWidth, imageHeight, new File(getChosenFile() + ".svg"));
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        } else if (formatResult == JOptionPane.NO_OPTION) {
            JFileChooser csvFileChooser = new JFileChooser();
            csvFileChooser.setDialogTitle("Choose CSV File Location");
            csvFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            csvFileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
            int csvResult = csvFileChooser.showSaveDialog(null);

            if (csvResult == JFileChooser.APPROVE_OPTION) {
                String csvFilePath = csvFileChooser.getSelectedFile().toString();
                if (!csvFilePath.endsWith(".csv")) {
                    csvFilePath += ".csv";
                }
                saveQueueingCharacteristicsToCSV(csvFilePath);
            }
        }
    }

}
