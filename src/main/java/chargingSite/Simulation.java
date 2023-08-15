package chargingSite;

import distributions.DistributionType;
import eventSimulation.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;
import queueingSystem.Client;
import queueingSystem.Queue.QueueingType;
import queueingSystem.QueueingSystem;
import results.Statistics;
import results.Times;

import javax.swing.*;
import java.awt.*;

import static org.jfree.chart.ChartFactory.createXYLineChart;

public class Simulation {
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

    private double MIN_ARRIVAL_RATE;
    private double MAX_ARRIVAL_RATE;
    private double ARRIVAL_RATE_STEP;
    private int SIM_STEPS = (int) Math.ceil((MAX_ARRIVAL_RATE - MIN_ARRIVAL_RATE) / ARRIVAL_RATE_STEP);;
    private int NUMBER_OF_CLIENT_TYPES;
    private int MAX_EVENTS;
    private int NUMBER_OF_SERVERS;
    private int QUEUE_SIZE;
    private QueueingType QUEUEING_TYPE;
    private double MEAN_SERVICE_TIME;
    private DistributionType ARRIVAL_TYPE;
    private DistributionType SERVICE_TYPE;
    private int confLevel;

    private static Times meanServiceTimes = new Times("ArrivalRate", "MeanServiceTime");
    private static Times meanQueueingTimes = new Times("ArrivalRate", "MeanQueueingTime");
    private static Times meanSystemTimes = new Times("ArrivalRate", "MeanSystemTime");


    public double getMIN_ARRIVAL_RATE() {
        return MIN_ARRIVAL_RATE;
    }

    public void setMIN_ARRIVAL_RATE(double MIN_ARRIVAL_RATE) {
        this.MIN_ARRIVAL_RATE = MIN_ARRIVAL_RATE;
    }

    public double getMAX_ARRIVAL_RATE() {
        return MAX_ARRIVAL_RATE;
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

    public void setSERVICE_TYPE(DistributionType SERVICE_TYPE) {
        this.SERVICE_TYPE = SERVICE_TYPE;
    }

    public void setConfLevel(int confLevel) {
        this.confLevel = confLevel;
    }

    public void runSimulation() {
        EventSimulation.setMaxEvents(MAX_EVENTS);
        Client[] myFirstClients = new Client[NUMBER_OF_CLIENT_TYPES];
        QueueingSystem mySystem = new QueueingSystem(NUMBER_OF_SERVERS,QUEUE_SIZE,QUEUEING_TYPE);
        if (NUMBER_OF_CLIENT_TYPES > 1) {
            mySystem.setName(ARRIVAL_TYPE + "/MIXED/" + NUMBER_OF_SERVERS + "/" + (NUMBER_OF_SERVERS+QUEUE_SIZE));
        } else {
            mySystem.setName(ARRIVAL_TYPE + "/" + SERVICE_TYPE + "/" + NUMBER_OF_SERVERS + "/" + (NUMBER_OF_SERVERS+QUEUE_SIZE));
        }
        mySystem.setDistributionType(ARRIVAL_TYPE);
        int stepCounter = 0;

        for (double arrivalRate = MIN_ARRIVAL_RATE; arrivalRate <= MAX_ARRIVAL_RATE; arrivalRate += ARRIVAL_RATE_STEP) {
            stepCounter++;
            mySystem.resetQueueingSystem();
            mySystem.setMeanInterArrivalTime(myFirstClients.length / arrivalRate); //mean inter-arrival time per client

            myFirstClients[0] = new Client(0.0, MEAN_SERVICE_TIME, SERVICE_TYPE, mySystem);  // set service time per client
            // add as manny client types as necessary -> adjust the numberOfClientTypes accordingly!
            //myFirstClients[1] = new Client(0.0, 0.5*MEAN_SERVICE_TIME, DistributionType.BETA, mySystem);  // set service time per client
            //myFirstClients[2] = new Client(0.0, 1.5*MEAN_SERVICE_TIME, DistributionType.EXPONENTIAL, mySystem);  // set service time per client

            EventSimulation.run(myFirstClients);

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


            Statistics calc = new Statistics();
            System.out.println("Mean Inter Arrival Time: " + 1.0 / arrivalRate);
            System.out.println("Service Time (" + mySystem.getTimesInService().size() + "): "
                    + calc.getMean(mySystem.getTimesInService()) + "/"
                    + calc.getStd(mySystem.getTimesInService()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInService(), 95));
            System.out.println("Queueing Time (" + mySystem.getTimesInQueue().size() + "): "
                    + calc.getMean(mySystem.getTimesInQueue()) + "/"
                    + calc.getStd(mySystem.getTimesInQueue()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInQueue(), 95));
            System.out.println("System Time (" + mySystem.getTimesInSystem().size() + "): "
                    + calc.getMean(mySystem.getTimesInSystem()) + "/"
                    + calc.getStd(mySystem.getTimesInSystem()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInSystem(), 95));

            System.out.print("Queue state: " + mySystem.getMyQueue().getOccupation());
            System.out.print("\t Server state: " + mySystem.getNumberOfServersInUse());
            System.out.println("\t Clients done: " + Client.getClientCounter());

            System.out.println(">--------- "+mySystem.getSystemName()+" Simulation step# " + stepCounter + " done -----------<");
        }

        drawGraph();



  /*      JFrame frame = new JFrame("Simulation Results");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 3)); // Arrange charts in a 1x3 grid

        ChartPanel meanServicePanel = meanServiceTimes.drawGraph("Mean Service Time", "Mean Service Time", "Arrival Rate");
        ChartPanel meanQueueingPanel = meanQueueingTimes.drawGraph("Mean Queueing Time", "Mean Queueing Time", "Arrival Rate");
        ChartPanel meanSystemPanel = meanSystemTimes.drawGraph("Mean System Time", "Mean System Time", "Arrival Rate");

       // ChartPanel ResultsPanel = meanServiceTimes.drawGraph("Mean Service Time", "Mean Service Time", "Arrival Rate");
        //ResultsPanel. // set it to actually add more graphs and not replace the existing...
       // ResultsPanel.add(meanQueueingTimes.drawGraph("Mean Queueing Time", "Mean Queueing Time", "Arrival Rate"));
       // ResultsPanel.add(meanSystemTimes.drawGraph("Mean Queueing Time", "Mean Queueing Time", "Arrival Rate"));

       // frame.add(ResultsPanel);


        frame.add(meanServicePanel);
        frame.add(meanQueueingPanel);
        frame.add(meanSystemPanel);

        frame.pack();
        frame.setVisible(true);
    } */
    }

    public void drawGraph() {                                  // Working version with confidence Interval
        int i = 0;
        String title = "Simulation Results";
        XYSeriesCollection dataset = new XYSeriesCollection();

        meanSystemTimes.addGraphs(dataset);
        meanServiceTimes.addGraphs(dataset);
        meanQueueingTimes.addGraphs(dataset);

        JFreeChart chart = createXYLineChart(
                title,
                "Arrival Rate",
                "Mean and Std",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        XYPlot plot = chart.getXYPlot();
        NumberAxis x_Axis = (NumberAxis) plot.getDomainAxis();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // draw first time results (time in system)
        i = 0;
        while( i < SIM_STEPS +1) {
            renderer.setSeriesPaint(i, Color.MAGENTA);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.magenta);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.magenta);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));

        // draw second time results (service times)
        while (i < 2 * (SIM_STEPS + 2) ) {
            renderer.setSeriesPaint(i, Color.blue);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.blue); // do wider line-width
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.blue);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));

        // draw third time results (queueing times)
        while (i < 3 * (SIM_STEPS + 2) +1) {
            renderer.setSeriesPaint(i, Color.red);
            renderer.setSeriesShape(i, ShapeUtilities.createRegularCross(0.5f, 1.5f));
            i++;
        }
        renderer.setSeriesPaint(i, Color.red);
        renderer.setSeriesStroke(i++, new BasicStroke(2.4f));
        renderer.setSeriesPaint(i, Color.red);
        renderer.setSeriesShape(i++, ShapeUtilities.createDiamond(0.75f));
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
        chartPanel.repaint();
    }
}




