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
import results.*;

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
import java.util.Map;
import java.util.logging.Logger;

import static chargingSite.ChargingSite.*;
import static org.jfree.chart.ChartFactory.createXYLineChart;
import static results.Histogram.generateHistogram;

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
    private JFreeChart SitePowerGraph;
    private static double MIN_ARRIVAL_RATE;
    private static double MAX_ARRIVAL_RATE;
    private static double ARRIVAL_RATE_STEP;
    private static int SIM_STEPS;
    private static int NUMBER_OF_CAR_TYPES;
    private static int MAX_EVENTS;
    private int NUMBER_OF_SERVERS;

    public static int MAX_SITE_POWER;  // Maximum Charging Site Power (50.000)
    public static int MAX_POINT_POWER; // Maximum Charging Point Power (750)
    public static int MAX_EV_POWER; // Maximum EV Charging Power (750)
    public static int MAX_EV_POWER2;
    public static int MAX_EV_POWER3;

    public static double MEAN_CHARGING_DEMAND;
    public static double MEAN_CHARGING_DEMAND2;
    public static double MEAN_CHARGING_DEMAND3;

    private int QUEUE_SIZE;
    private QueueingType QUEUEING_TYPE;
    private double MEAN_SERVICE_TIME;
    private double MEAN_SERVICE_TIME2;
    private double MEAN_SERVICE_TIME3;
    public static DistributionType SERVICE_TYPE;
    private double AVERAGE_SERVICE_TIME;
    private DistributionType ARRIVAL_TYPE;
    private DistributionType DEMAND_TYPE;
    private DistributionType DEMAND_TYPE2;
    private DistributionType DEMAND_TYPE3;
    private int confLevel;
    public static double batteryCapacity;
    public static double batteryCapacity2;
    public static double batteryCapacity3;

    private double percentageOfCars = 1;
    private double percentageOfCars2;
    private double percentageOfCars3;
    public Monitor chargingMonitor;


    private final Times meanServiceTimes = new Times("ArrivalRate", "MeanServiceTime");
    private final Times meanQueueingTimes = new Times("ArrivalRate", "MeanQueueingTime");
    private final Times meanSystemTimes = new Times("ArrivalRate", "MeanSystemTime");
    private final XYSeries analyticWaitingTimes = new XYSeries("Value");

    //----------------private Monitor meanEnergyCharged = new Monitor();// collect mean, std, confidence


    public DistributionType getSERVICE_TYPE() {
        return SERVICE_TYPE;
    }

    public static int getSIM_STEPS() {
        return SIM_STEPS;
    }


    public void setMIN_ARRIVAL_RATE(double MIN_ARRIVAL_RATE) {
        Simulation.MIN_ARRIVAL_RATE = MIN_ARRIVAL_RATE;
    }

    public static void setMAX_ARRIVAL_RATE(double MAX_ARRIVAL_RATE) {
        Simulation.MAX_ARRIVAL_RATE = MAX_ARRIVAL_RATE;
    }

    public static double getARRIVAL_RATE_STEP() {
        return ARRIVAL_RATE_STEP;
    }

    public static void setARRIVAL_RATE_STEP(double ARRIVAL_RATE_STEP) {
        Simulation.ARRIVAL_RATE_STEP = ARRIVAL_RATE_STEP;
    }


    public static void setSIM_STEPS(int SIM_STEPS) {
        Simulation.SIM_STEPS = SIM_STEPS;
    }

    public static void setBatteryCapacity(double batteryCapacity) {
        Simulation.batteryCapacity = batteryCapacity;
    }

    public static void setNUMBER_OF_CAR_TYPES(int NUMBER_OF_CAR_TYPES) {
        Simulation.NUMBER_OF_CAR_TYPES = NUMBER_OF_CAR_TYPES;
    }

    public void setMAX_EVENTS(int MAX_EVENTS) {
        Simulation.MAX_EVENTS = MAX_EVENTS;
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

    public void setAVERAGE_SERVICE_TIME(double AVERAGE_SERVICE_TIME) {
        this.AVERAGE_SERVICE_TIME = AVERAGE_SERVICE_TIME;
    }

    public void setARRIVAL_TYPE(DistributionType ARRIVAL_TYPE) {
        this.ARRIVAL_TYPE = ARRIVAL_TYPE;
    }

    public void setDEMAND_TYPE(DistributionType DEMAND_TYPE) {
        this.DEMAND_TYPE = DEMAND_TYPE;
    }

    public static void setSERVICE_TYPE(DistributionType type) {
        SERVICE_TYPE = type;
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


    public static void setMaxEvPower2(int maxEvPower2) {
        MAX_EV_POWER2 = maxEvPower2;
    }

    public static void setMaxEvPower3(int maxEvPower3) {
        MAX_EV_POWER3 = maxEvPower3;
    }

    public static void setMeanChargingDemand2(double meanChargingDemand2) {
        MEAN_CHARGING_DEMAND2 = meanChargingDemand2;
    }

    public static void setMeanChargingDemand3(double meanChargingDemand3) {
        MEAN_CHARGING_DEMAND3 = meanChargingDemand3;
    }

    public void setDEMAND_TYPE2(DistributionType DEMAND_TYPE2) {
        this.DEMAND_TYPE2 = DEMAND_TYPE2;
    }

    public void setDEMAND_TYPE3(DistributionType DEMAND_TYPE3) {
        this.DEMAND_TYPE3 = DEMAND_TYPE3;
    }

    public static void setBatteryCapacity2(double batteryCapacity2) {
        Simulation.batteryCapacity2 = batteryCapacity2;
    }

    public static void setBatteryCapacity3(double batteryCapacity3) {
        Simulation.batteryCapacity3 = batteryCapacity3;
    }

    public void setPercentageOfCars(double percentageOfCars) {
        if (percentageOfCars > 1) percentageOfCars /= 100;
        if (percentageOfCars < 0)
            System.out.println("ERROR: percentage of type 1 cars " + percentageOfCars + "is smaller than 0!");
        this.percentageOfCars = percentageOfCars;
    }

    public void setPercentageOfCars2(double percentageOfCars) {
        if (percentageOfCars > 1) percentageOfCars /= 100;
        if (percentageOfCars < 0)
            System.out.println("ERROR: percentage of type 1 cars " + percentageOfCars + "is smaller than 0!");
        this.percentageOfCars2 = percentageOfCars;
    }

    public void setPercentageOfCars3(double percentageOfCars) {
        if (percentageOfCars > 1) percentageOfCars /= 100;
        if (percentageOfCars < 0)
            System.out.println("ERROR: percentage of type 1 cars " + percentageOfCars + "is smaller than 0!");
        this.percentageOfCars3 = percentageOfCars;
    }

    public void setMEAN_SERVICE_TIME(double time) {
        if (time == 0) System.out.println("Warning: charging till 80% is yet not implemented correctly!");
        this.MEAN_SERVICE_TIME = time;
    }

    public void setMEAN_SERVICE_TIME2(double time) {
        this.MEAN_SERVICE_TIME2 = time;
    }

    public void setMEAN_SERVICE_TIME3(double time) {
        this.MEAN_SERVICE_TIME3 = time;
    }

    public int getNUMBER_OF_CAR_TYPES() {
        return NUMBER_OF_CAR_TYPES;
    }

    public int getMaxEvPower() {
        return MAX_EV_POWER;
    }

    public int getMaxEvPower2() {
        return MAX_EV_POWER2;
    }

    public int getMaxEvPower3() {
        return MAX_EV_POWER3;
    }

    public double getMeanChargingDemand() {
        return MEAN_CHARGING_DEMAND;
    }

    public double getMeanChargingDemand2() {
        return MEAN_CHARGING_DEMAND2;
    }

    public double getMeanChargingDemand3() {
        return MEAN_CHARGING_DEMAND3;
    }

    public double getMEAN_SERVICE_TIME() {
        return MEAN_SERVICE_TIME;
    }

    public double getMEAN_SERVICE_TIME2() {
        return MEAN_SERVICE_TIME2;
    }

    public double getMEAN_SERVICE_TIME3() {
        return MEAN_SERVICE_TIME3;
    }

    public DistributionType getDEMAND_TYPE() {
        return DEMAND_TYPE;
    }

    public DistributionType getDEMAND_TYPE2() {
        return DEMAND_TYPE2;
    }

    public DistributionType getDEMAND_TYPE3() {
        return DEMAND_TYPE3;
    }

    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    public double getBatteryCapacity2() {
        return batteryCapacity2;
    }

    public double getBatteryCapacity3() {
        return batteryCapacity3;
    }

    public double getPercentageOfCars() {
        return percentageOfCars;
    }

    public double getPercentageOfCars2() {
        return percentageOfCars2;
    }

    public double getPercentageOfCars3() {
        return percentageOfCars3;
    }

    public static int getSimSteps() {
        return SIM_STEPS;
    }

    public static double getMaxArrivalRate() {
        return MAX_ARRIVAL_RATE;
    }

    public static int getMaxEvents() {
        return MAX_EVENTS;
    }

    public Simulation() {
        calcAvgServiceTime();
    }

    private double calcAvgServiceTime() {
        double p1, p2, p3;
        if (getNUMBER_OF_CAR_TYPES() < 2) AVERAGE_SERVICE_TIME = MEAN_SERVICE_TIME;
        else if (getNUMBER_OF_CAR_TYPES() < 3) {
            if (getPercentageOfCars2() > 1) p2 = getPercentageOfCars2() / 100;
            else p2 = getPercentageOfCars2();
            p1 = 1 - p2;
            AVERAGE_SERVICE_TIME = p1 * MEAN_SERVICE_TIME + p2 * MEAN_SERVICE_TIME2;
        } else {
            if (getPercentageOfCars2() > 1) p2 = getPercentageOfCars2() / 100;
            else p2 = getPercentageOfCars2();
            if (getPercentageOfCars3() > 1) p3 = getPercentageOfCars3() / 100;
            else p3 = getPercentageOfCars3();
            p1 = 1 - p2 - p3;
            AVERAGE_SERVICE_TIME = p1 * MEAN_SERVICE_TIME + p2 * MEAN_SERVICE_TIME2 + p3 * MEAN_SERVICE_TIME3;
        }
        return AVERAGE_SERVICE_TIME;
    }


    public double calcMMnNwaitingTime(double rho) {
        double meanWaitingTime;
        double arrivalRate = rho * this.NUMBER_OF_SERVERS / this.AVERAGE_SERVICE_TIME;
        double[] pdi = new double[1 + this.NUMBER_OF_SERVERS + this.QUEUE_SIZE];
        double meanQueueLength = 0;
        double sFac = Distribution.factorial(this.NUMBER_OF_SERVERS);
        rho *= this.NUMBER_OF_SERVERS;
        pdi[0] = 0;
        for (int i = 1; i <= this.QUEUE_SIZE; i++) {
            pdi[0] += Math.pow(rho / this.NUMBER_OF_SERVERS, i);
        }
        pdi[0] *= Math.pow(rho, this.NUMBER_OF_SERVERS) / sFac;
        for (int i = 1; i <= this.NUMBER_OF_SERVERS; i++) {
            pdi[0] += Math.pow(rho, i) / Distribution.factorial(i);
        }
        pdi[0] = Math.pow(1 + pdi[0], -1);

        for (int i = 1; i <= this.NUMBER_OF_SERVERS; i++) {
            pdi[i] = Math.pow(rho, i) / Distribution.factorial(i) * pdi[0];
        }
        for (int i = this.NUMBER_OF_SERVERS + 1; i <= this.NUMBER_OF_SERVERS + this.QUEUE_SIZE; i++) {
            pdi[i] = Math.pow(rho, i) / (sFac * Math.pow(this.NUMBER_OF_SERVERS, i - this.NUMBER_OF_SERVERS)) * pdi[0];
        }

        for (int i = 1; i <= this.QUEUE_SIZE; i++) {
            meanQueueLength += i * pdi[this.NUMBER_OF_SERVERS + i];
        }

        meanWaitingTime = meanQueueLength / (arrivalRate * (1 - pdi[this.NUMBER_OF_SERVERS + this.QUEUE_SIZE]));
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
        resetData();
        EventSimulation.setMaxEvents(MAX_EVENTS);
        Client[] myFirstClients = new Client[1];
        Client myFirstClient;
        QueueingSystem mySystem = new QueueingSystem(NUMBER_OF_SERVERS, QUEUE_SIZE, QUEUEING_TYPE);
        chargingMonitor.setSource(mySystem);
        if (NUMBER_OF_CAR_TYPES > 1) {
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

            // add as manny client types as necessary -> adjust the numberOfClientTypes accordingly!
        /*    if (NUMBER_OF_CLIENT_TYPES>2) {
                myFirstClients[1] = new Client(0.0, PERCENTAGE_OF_CARS_3*MEAN_SERVICE_TIME_3, SERVICE_TYPE_3, mySystem);  // set service time per client
            }
            if (NUMBER_OF_CLIENT_TYPES>1) {
                myFirstClients[1] = new Client(0.0, PERCENTAGE_OF_CARS_2*MEAN_SERVICE_TIME_2, SERVICE_TYPE_2, mySystem);  // set service time per client
            }
        */
            //for (int i=0; i < myFirstClients.length; i++) {
            myFirstClients[0] = new Client(0.0, ElectricVehicle.createRandomCar(this), mySystem);
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
            plotHistogram(mySystem.getChargingSite().getSitePower1(), 20);


            mySystem.getChargingSite().displayChart(dataList);


           // mySystem.getChargingSite().visualizeSitePower();
          //  mySystem.getChargingSite().displayChart();

         //   ChartGenerator.initializeChart();
          //  ChartGenerator.displayChart(dataList, ChartGenerator.frame);




            //dummy.add(this.calcMMnNwaitingTime(arrivalRate * this.MEAN_SERVICE_TIME / this.NUMBER_OF_SERVERS));
            analyticWaitingTimes.add(arrivalRate, this.calcMMnNwaitingTime(arrivalRate * calcAvgServiceTime() / this.NUMBER_OF_SERVERS));

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

        //plotHistogram(mySystem.getChargingSite().getSitePower1(), 15);
    }

    public void drawGraph() {   // D/D/5/10 Queueing System

        String title = "Charging Site Queueing Characteristics \n"
                + this.getKendallName() + " Queueing System"
                + " (" + MAX_EVENTS + " samples per evaluation point)";

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
        if (AVERAGE_SERVICE_TIME > 0) {
            yAxis.setRange(0, Math.ceil(1.1 * AVERAGE_SERVICE_TIME * (1 + QUEUE_SIZE / NUMBER_OF_SERVERS)));
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
                items.add(new LegendItem("M/M/n/N Queueing Time", Color.BLACK));

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