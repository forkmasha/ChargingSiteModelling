package chargingSite;

import distributions.Distribution;
import distributions.DistributionType;
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
import queueingSystem.Queue.QueueingType;
import queueingSystem.QueueingSystem;
import results.Graph;
import results.Monitor;
import results.Statistics;
import results.Times;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.jfree.chart.ChartFactory.createXYLineChart;;

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
    private JFreeChart MyChart;
    private double MIN_ARRIVAL_RATE;
    private double MAX_ARRIVAL_RATE;
    private double ARRIVAL_RATE_STEP;
    private int SIM_STEPS;
    private int NUMBER_OF_CLIENT_TYPES;
    private int MAX_EVENTS;
    private int NUMBER_OF_SERVERS;

    public static int MAX_SITE_POWER;  // Maximum Charging Site Power (50.000)
    public static int MAX_POINT_POWER; // Maximum Charging Point Power (750)
    public static int MAX_EV_POWER; // Maximum EV Charging Power (750)

    public static double MEAN_CHARGING_DEMAND;

    private int QUEUE_SIZE;
    private QueueingType QUEUEING_TYPE;
    private double MEAN_SERVICE_TIME;
    private DistributionType ARRIVAL_TYPE;
    private DistributionType SERVICE_TYPE;
    private DistributionType DEMAND_TYPE;
    private int confLevel;
    public static double batteryCapacity;
    public Monitor chargingMonitor;


    private Times meanServiceTimes = new Times("ArrivalRate", "MeanServiceTime");
    private Times meanQueueingTimes = new Times("ArrivalRate", "MeanQueueingTime");
    private Times meanSystemTimes = new Times("ArrivalRate", "MeanSystemTime");
    private XYSeries analyticWaitingTimes = new XYSeries("Value");

    //----------------private Monitor meanEnergyCharged = new Monitor();// collect mean, std, confidence


    public DistributionType getSERVICE_TYPE() {
        return SERVICE_TYPE;
    }

    public int getSIM_STEPS() {
        return SIM_STEPS;
    }

    public void setMIN_ARRIVAL_RATE(double MIN_ARRIVAL_RATE) {
        this.MIN_ARRIVAL_RATE = MIN_ARRIVAL_RATE;
    }

    public void setMAX_ARRIVAL_RATE(double MAX_ARRIVAL_RATE) {
        this.MAX_ARRIVAL_RATE = MAX_ARRIVAL_RATE;
    }

    public double getARRIVAL_RATE_STEP() {
        return ARRIVAL_RATE_STEP;
    }

    public void setARRIVAL_RATE_STEP(double ARRIVAL_RATE_STEP) {
        this.ARRIVAL_RATE_STEP = ARRIVAL_RATE_STEP;
    }


    public void setSIM_STEPS(int SIM_STEPS) {
        this.SIM_STEPS = SIM_STEPS;
    }

    public void setBatteryCapacity(double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public void setNUMBER_OF_CLIENT_TYPES(int NUMBER_OF_CLIENT_TYPES) {
        this.NUMBER_OF_CLIENT_TYPES = NUMBER_OF_CLIENT_TYPES;
    }

    public void setMAX_EVENTS(int MAX_EVENTS) {
        this.MAX_EVENTS = MAX_EVENTS;
    }

    public void setNUMBER_OF_SERVERS(int NUMBER_OF_SERVERS) {
        this.NUMBER_OF_SERVERS = NUMBER_OF_SERVERS;
    }

    public void setQUEUE_SIZE(int QUEUE_SIZE) {
        this.QUEUE_SIZE = QUEUE_SIZE;
    }

    public void setQUEUEING_TYPE(QueueingType QUEUEING_TYPE) {
        this.QUEUEING_TYPE = QUEUEING_TYPE;
    }

    public void setMEAN_SERVICE_TIME(double MEAN_SERVICE_TIME) {
        this.MEAN_SERVICE_TIME = MEAN_SERVICE_TIME;
    }

    public void setARRIVAL_TYPE(DistributionType ARRIVAL_TYPE) {
        this.ARRIVAL_TYPE = ARRIVAL_TYPE;
    }

    public void setDEMAND_TYPE(DistributionType DEMAND_TYPE) {
        this.DEMAND_TYPE = DEMAND_TYPE;
    }

    public void setSERVICE_TYPE(DistributionType SERVICE_TYPE) {
        this.SERVICE_TYPE = SERVICE_TYPE;
    }

    public void setConfLevel(int confLevel) {
        this.confLevel = confLevel;
        this.chargingMonitor = new Monitor(confLevel);
    }

    public static void setMaxSitePower(int maxSitePower) {
        MAX_SITE_POWER = maxSitePower;
    }

    public static void setMeanChargingDemand(double meanChargingDemand) {
        MEAN_CHARGING_DEMAND = meanChargingDemand;
    }

    public static void setMaxPointPower(int maxPointPower) {
        MAX_POINT_POWER = maxPointPower;
    }

    public static void setMaxEvPower(int maxEvPower) {
        MAX_EV_POWER = maxEvPower;
    }

    public Simulation() {
    }

    public double calcMMnNwaitingTime(double rho) {
        double meanWaitingTime;
        double arrivalRate = rho * this.NUMBER_OF_SERVERS / this.MEAN_SERVICE_TIME;
        double[] pdi = new double[1+this.NUMBER_OF_SERVERS+this.QUEUE_SIZE];
        double meanQueueLength = 0;
        double sFac = Distribution.factorial(this.NUMBER_OF_SERVERS);
        pdi[0] = 0;
        for (int i = 1; i <= this.QUEUE_SIZE; i++) {
            pdi[0] += Math.pow(rho / this.NUMBER_OF_SERVERS, i);
        }
        pdi[0] *= Math.pow(rho,this.NUMBER_OF_SERVERS)/Distribution.factorial(this.NUMBER_OF_SERVERS);
        for (int i = 1; i <= this.NUMBER_OF_SERVERS; i++) {
            pdi[0] += Math.pow(rho,i)/Distribution.factorial(i);
        }
        pdi[0] += 1;
        pdi[0] = 1/pdi[0];

        for (int i = 1; i <= this.NUMBER_OF_SERVERS; i++) {
            pdi[i] = Math.pow(rho,i)/Distribution.factorial(i) * pdi[0];
        }
        for (int i = this.NUMBER_OF_SERVERS+1; i <= this.NUMBER_OF_SERVERS+this.QUEUE_SIZE; i++) {
            pdi[i] = Math.pow(rho,i)/(sFac * Math.pow(this.NUMBER_OF_SERVERS,i-this.NUMBER_OF_SERVERS));
        }

        for (int i = 1; i <= this.QUEUE_SIZE; i++) {
            meanQueueLength += i * pdi[i+this.NUMBER_OF_SERVERS];
        }

        meanWaitingTime = meanQueueLength / (arrivalRate * (1-pdi[this.NUMBER_OF_SERVERS+this.QUEUE_SIZE]));
        if (Math.abs(1 - Arrays.stream(pdi).sum()) > 0.000001) {
            System.out.println("ERROR: sum over all state-probabilities !=1");
            StringBuilder output = new StringBuilder("M/M/n/S state probabilities: ");
            for (int i = 0; i < pdi.length; i++) {
                output.append(pdi[i]).append(" / ");
            }
            System.out.println("M/M/n/S state probabilities: " + output);
        }
        System.out.println("M/M/n/S: " + arrivalRate + " " + meanQueueLength + " " + meanWaitingTime);
        return meanWaitingTime;
    }


    public void saveAsSVG(int wi, int hi, File svgFile) throws IOException {

        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);


        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);


        MyChart.draw(svgGenerator, new Rectangle2D.Double(0, 0, wi, hi));


        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(svgFile), StandardCharsets.UTF_8)) {
            svgGenerator.stream(writer, true);
        }
    }

    public String getKendallName() {
        return Distribution.getTitleAbbreviation(String.valueOf(ARRIVAL_TYPE)) + "/"
                + Distribution.getTitleAbbreviation(String.valueOf(SERVICE_TYPE)) + "/"
                + NUMBER_OF_SERVERS + "/" + (NUMBER_OF_SERVERS + QUEUE_SIZE);
    }


    public void runSimulation() {
        EventSimulation.setMaxEvents(MAX_EVENTS);
        Client[] myFirstClients = new Client[NUMBER_OF_CLIENT_TYPES];
        QueueingSystem mySystem = new QueueingSystem(NUMBER_OF_SERVERS, QUEUE_SIZE, QUEUEING_TYPE);
        chargingMonitor.setSource(mySystem);
        if (NUMBER_OF_CLIENT_TYPES > 1) {
            mySystem.setName(Distribution.getTitleAbbreviation(ARRIVAL_TYPE.toString())
                    + "/MIXED/"
                    + NUMBER_OF_SERVERS + "/" + (NUMBER_OF_SERVERS + QUEUE_SIZE));
        } else {
            mySystem.setName(Distribution.getTitleAbbreviation(ARRIVAL_TYPE.toString())
                    + "/" + Distribution.getTitleAbbreviation(SERVICE_TYPE.toString())
                    + "/" + NUMBER_OF_SERVERS + "/" + (NUMBER_OF_SERVERS + QUEUE_SIZE));
        }
        mySystem.setDistributionType(ARRIVAL_TYPE);
        int stepCounter = 0;
        double arrivalRate = MIN_ARRIVAL_RATE;
        List<Double> dummy = new ArrayList<>();

        //for (double arrivalRate = MIN_ARRIVAL_RATE; arrivalRate <= MAX_ARRIVAL_RATE; arrivalRate += ARRIVAL_RATE_STEP) {
        while (stepCounter < SIM_STEPS) {
            if (arrivalRate > MAX_ARRIVAL_RATE + 0.001)
                System.out.println("WARNING: Arrival rate " + arrivalRate + " beyond maximum " + MAX_ARRIVAL_RATE + " occurred!");
            stepCounter++;
            mySystem.resetQueueingSystem();
            mySystem.setMeanInterArrivalTime(myFirstClients.length / arrivalRate); //mean inter-arrival time per client

            myFirstClients[0] = new Client(0.0, MEAN_SERVICE_TIME, SERVICE_TYPE, mySystem);  // set service time per client
            // add as manny client types as necessary -> adjust the numberOfClientTypes accordingly!
            //  myFirstClients[1] = new Client(0.0, 0.5*MEAN_SERVICE_TIME, DistributionType.BETA, mySystem);  // set service time per client
            // myFirstClients[2] = new Client(0.0, 1.5*MEAN_SERVICE_TIME, DistributionType.EXPONENTIAL, mySystem);  // set service time per client

            EventSimulation.run(myFirstClients);

            //dummy.add(this.calcMMnNwaitingTime(arrivalRate * this.MEAN_SERVICE_TIME / this.NUMBER_OF_SERVERS));
            analyticWaitingTimes.add(arrivalRate,this.calcMMnNwaitingTime(arrivalRate * this.MEAN_SERVICE_TIME / this.NUMBER_OF_SERVERS));

            meanServiceTimes.addStep(arrivalRate);
            meanServiceTimes.addMean(mySystem.getTimesInService());
            meanServiceTimes.addStds(mySystem.getTimesInService());
            meanServiceTimes.addConfidence(mySystem.getTimesInService(), confLevel);

            meanQueueingTimes.addStep(arrivalRate);
            meanQueueingTimes.addMean(mySystem.getTimesInQueue());
            meanQueueingTimes.addStds(mySystem.getTimesInQueue());
            meanQueueingTimes.addConfidence(mySystem.getTimesInQueue(), confLevel);

            meanSystemTimes.addStep(arrivalRate);
            meanSystemTimes.addMean(mySystem.getTimesInSystem());
            meanSystemTimes.addStds(mySystem.getTimesInSystem());
            meanSystemTimes.addConfidence(mySystem.getTimesInSystem(), confLevel);

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
                    + calc.getConfidenceInterval(mySystem.getTimesInService(), this.confLevel)
                    + "\n Queueing Time (" + mySystem.getTimesInQueue().size() + "): "
                    + calc.getMean(mySystem.getTimesInQueue()) + "/"
                    + calc.getStd(mySystem.getTimesInQueue()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInQueue(), this.confLevel)
                    + "\n System Time (" + mySystem.getTimesInSystem().size() + "): "
                    + calc.getMean(mySystem.getTimesInSystem()) + "/"
                    + calc.getStd(mySystem.getTimesInSystem()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInSystem(), this.confLevel)
                    + "\n Charged Energy (" + mySystem.getAmountsCharged().size() + "): "
                    + calc.getMean(mySystem.getAmountsCharged()) + "/"
                    + calc.getStd(mySystem.getAmountsCharged()) + "/"
                    + calc.getConfidenceInterval(mySystem.getAmountsCharged(), this.confLevel)
                    + "\n Site Power Demand (" + mySystem.getSitePowers().size() + "): "
                    + calc.getMean(mySystem.getSitePowers()) + "/"
                    + calc.getStd(mySystem.getSitePowers()) + "/"
                    + calc.getConfidenceInterval(mySystem.getSitePowers(), this.confLevel)
                    + "\n Queue state: " + mySystem.getMyQueue().getOccupation()
                    + " Server state: " + mySystem.getNumberOfServersInUse()
                    + " Clients done: " + Client.getClientCounter()
            );

            arrivalRate += ARRIVAL_RATE_STEP;
        }
        drawGraph();

        chargingMonitor.drawGraph(this);
    }

    public void drawGraph() {   // D/D/5/10 Queueing System

        String title = "Charging Site Queueing Characteristics \n"
                + this.getKendallName() + " Queueing System"
                + " (" + this.MAX_EVENTS + " samples per evaluation point)";

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
        if (MEAN_SERVICE_TIME > 0) {
            yAxis.setRange(0, Math.ceil(1.1 * MEAN_SERVICE_TIME * (1 + QUEUE_SIZE / NUMBER_OF_SERVERS)));
        } else {
            yAxis.setRange(0, 3);
        }

        int i = 0;
        while (i < SIM_STEPS) {
            renderer.setSeriesPaint(i, Color.MAGENTA);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.magenta);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.magenta);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));


        while (i < 2 * SIM_STEPS + 2) {
            renderer.setSeriesPaint(i, Color.blue);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.blue);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.blue);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));


        while (i < 3 * SIM_STEPS + 4) {
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

                // Add legend items for each series
                items.add(new LegendItem("Service Time", Color.blue));
                items.add(new LegendItem("Queuing Time", Color.RED));
                items.add(new LegendItem("System Time", Color.MAGENTA));
                items.add(new LegendItem("Analytical Queueing Time",Color.BLACK));

                return items;
            }
        };

        plot.setFixedLegendItems(legendItemSource.getLegendItems());

        ChartPanel chartPanel = new ChartPanel(MyChart);
        chartPanel.setPreferredSize(new Dimension(800, 630));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        });

        frame.setContentPane(chartPanel);
        frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        frame.setLocation(screenWidth - frame.getWidth(), 0);

        frame.setVisible(true);
        chartPanel.repaint();
    }

    public void saveSVGDialogue() {
        Object[] options = {"SVG", "CSV"};
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
            // Save as SVG
            boolean inputValid = getUserInput() && chooseFile();

            if (inputValid) {
                try {
                    int imageWidth = Integer.parseInt(getWidthField().getText());
                    int imageHeight = Integer.parseInt(getHeightField().getText());
                    saveAsSVG(imageWidth, imageHeight, new File(getChosenFile() + ".svg"));
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
                saveGraphDataToCSV(csvFilePath);
            }
        }
    }

    public void saveGraphDataToCSV(String filePath) {
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
                writer.append(arrivalRates.get(i).toString());
                writer.append(";");
                writer.append(meanServiceTimeValues.get(i).toString());
                writer.append(";");
                writer.append(stdServiceTimeValues.get(i).toString());
                writer.append(";");
                writer.append(confServiceTimeValues.get(i).toString());
                writer.append(";");
                writer.append(meanQueueingTimeValues.get(i).toString());
                writer.append(";");
                writer.append(stdQueueingTimeValues.get(i).toString());
                writer.append(";");
                writer.append(confQueueingTimeValues.get(i).toString());
                writer.append(";");
                writer.append(meanSystemTimeValues.get(i).toString());
                writer.append(";");
                writer.append(stdSystemTimeValues.get(i).toString());
                writer.append(";");
                writer.append(confSystemTimeValues.get(i).toString());
                writer.append("\n");
            }
            System.out.println("CSV file has been created successfully!");

        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }
}