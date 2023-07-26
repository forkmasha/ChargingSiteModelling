package chargingSite;

import distributions.DistributionType;
import eventSimulation.*;
import queueingSystem.Client;
import queueingSystem.QueueingSystem;
import results.Statistics;
import results.Times;


public class Simulation {
    private static final double MIN_ARRIVAL_RATE = 0.5;
    private static final double MAX_ARRIVAL_RATE = 20.0;
    private static final double ARRIVAL_RATE_STEP = 0.5;
    private static final int MAX_EVENTS = 1000;
    private static final int NUMBER_OF_SERVERS = 5;
    private static final int QUEUE_SIZE = 10;
    private static final double MEAN_SERVICE_TIME = 0.5;

    private static int confLevel=95;
    private static Times meanServiceTimes = new Times("ArrivalRate","MeanServiceTime");
    private static Times meanQueueingTimes = new Times("ArrivalRate","MeanQueueingTime");
    private static Times meanSystemTimes = new Times("ArrivalRate","MeanSystemTime");

    public void runSimulation() {
        EventSimulation.setMaxEvents(MAX_EVENTS);
        QueueingSystem mySystem = new QueueingSystem();
        mySystem.setNumberOfServers(NUMBER_OF_SERVERS);
        mySystem.setDistributionType(DistributionType.EXPONENTIAL);
        mySystem.setQueueSize(QUEUE_SIZE);

        for (double arrivalRate = MIN_ARRIVAL_RATE; arrivalRate <= MAX_ARRIVAL_RATE; arrivalRate += ARRIVAL_RATE_STEP) {
            mySystem.setMeanInterArrivalTime(1.0 / arrivalRate); //mean inter-arrival time

            Client myFirstClient = new Client(MEAN_SERVICE_TIME);  // mean service time
            myFirstClient.setServiceTimeDistribution(DistributionType.UNIFORM);
            myFirstClient.setSystem(mySystem);


            EventSimulation.run(myFirstClient);

            meanServiceTimes.addMean(mySystem.getTimesInService());
            meanServiceTimes.addStds(mySystem.getTimesInService());
            meanServiceTimes.addConfidence(mySystem.getTimesInService(),confLevel);

            meanQueueingTimes.addMean(mySystem.getTimesInQueue());
            meanQueueingTimes.addStds(mySystem.getTimesInQueue());
            meanQueueingTimes.addConfidence(mySystem.getTimesInQueue(),confLevel);

            meanSystemTimes.addMean(mySystem.getTimesInSystem());
            meanSystemTimes.addStds(mySystem.getTimesInSystem());
            meanSystemTimes.addConfidence(mySystem.getTimesInSystem(),confLevel);


            Statistics calc = new Statistics();
            System.out.println("Mean Inter Arrival Time: " + 1.0 / arrivalRate);
            System.out.println("Service Time: " + calc.getMean(mySystem.getTimesInService()) + "/"
                    + calc.getStd(mySystem.getTimesInService()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInService(), 95));
            System.out.println("Queueing Time: " + calc.getMean(mySystem.getTimesInQueue()) + "/"
                    + calc.getStd(mySystem.getTimesInQueue()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInQueue(), 95));
            System.out.println("System Time: " + calc.getMean(mySystem.getTimesInSystem()) + "/"
                    + calc.getStd(mySystem.getTimesInSystem()) + "/"
                    + calc.getConfidenceInterval(mySystem.getTimesInSystem(), 95));


            System.out.println("----------------------");
        }
        //meanServiceTimes.drawGraph();
        //meanQueueingTimes.drawGraph();
        //meanSystemTimes.drawGraph();

        //meanSystemTimes.createLineChart();
        meanSystemTimes.plotGraph();
    }
}




