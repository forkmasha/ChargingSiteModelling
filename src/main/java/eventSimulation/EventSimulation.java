package eventSimulation;

import distributions.DistributionType;
import queueingSystem.Client;
import queueingSystem.QueueingSystem;
import results.Statistics;

public class EventSimulation {
    private static int maxEvents = 100000;
    private static int numberOfEvents = 0;
    private static double currentTime = 0;
    public static double meanServiceTime = 0.5;

    public static void setMaxEvents(int number) {
        EventSimulation.maxEvents = number;
    }

    public static void reset() {
        EventSimulation.numberOfEvents = 0;
        EventSimulation.currentTime = 0;
        EventProcessor.reset();

    }

    public static void incNumberOfEvents() {
        numberOfEvents++;
    }

    public static int getMaxEvents() {
        return maxEvents;
    }

    public static int getNumberOfEvents() {
        return numberOfEvents;
    }

    public static double getCurrentTime() {
        return currentTime;
    }

    public static void setCurrentTime(double currentTime) {
        EventSimulation.currentTime = currentTime;
    }

    public static EventProcessor eventProcessor = new EventProcessor();
    public static EventStack eventStack = new EventStack();

    //public static QueueingSystem system = new QueueingSystem();
    public static void run(Client myClient) {
        EventSimulation.reset();
        Event firstEvent = new Event(0.0); // execution time
        firstEvent.setEventType(EventType.ARRIVAL);
        firstEvent.setClient(myClient);
        eventStack.addEvent(firstEvent);
        //maxEvents = 1000;
        while (!eventStack.isEmpty()) {
            eventProcessor.processEvent(eventStack.getNextEvent());
        }
        eventProcessor.printCounters();

    }

 /*   public static void main(String[] args) {
        QueueingSystem mySystem = new QueueingSystem();
        mySystem.setNumberOfServers(5);
        mySystem.setDistributionType(DistributionType.EXPONENTIAL);
        mySystem.setMeanInterArrivalTime(0.1); //mean inter-arrival time
        mySystem.setQueueSize(10);
        Client myClient = new Client(0.5);  // mean service time
        myClient.setServiceTimeDistribution(DistributionType.UNIFORM);
        myClient.setSystem(mySystem);
        Event firstEvent = new Event(0.0); // execution time
        firstEvent.setEventType(EventType.ARRIVAL);
        firstEvent.setClient(myClient);
        eventStack.addEvent(firstEvent);
        maxEvents = 1000;
        while (!eventStack.isEmpty()) {
            eventProcessor.processEvent(eventStack.getNextEvent());
        }
        eventProcessor.printCounters();
        Statistics calc = new Statistics();
        System.out.println("Service Time: "+calc.getMean(mySystem.getTimesInService()) + "/"
                + calc.getStd(mySystem.getTimesInService()) + "/"
                + calc.getConfidenceInterval(mySystem.getTimesInService(),95));
        System.out.println("Queueing Time: "+calc.getMean(mySystem.getTimesInQueue()) + "/"
                + calc.getStd(mySystem.getTimesInQueue()) + "/"
                + calc.getConfidenceInterval(mySystem.getTimesInQueue(),95));
        System.out.println("System Time: "+calc.getMean(mySystem.getTimesInSystem()) + "/"
                + calc.getStd(mySystem.getTimesInSystem()) + "/"
                + calc.getConfidenceInterval(mySystem.getTimesInSystem(),95));

    }*/
}