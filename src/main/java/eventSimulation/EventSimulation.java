package eventSimulation;

import distributions.DistributionType;
import queueingSystem.Client;
import queueingSystem.QueueingSystem;

public class EventSimulation {
    private static int maxEvents = 100000;
    private static int numberOfEvents = 0;
    private static double currentTime;

    public void setMaxEvents(int maxEvents) {
        this.maxEvents = maxEvents;
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

    public static void main(String[] args) {
        Event firstEvent = new Event(0);
        firstEvent.setEventType(EventType.ARRIVAL);
        Client myClient = new Client(0.5);
        myClient.setServiceTimeDistribution(DistributionType.UNIFORM);
        QueueingSystem mySystem = new QueueingSystem();
        mySystem.setDistributionType(DistributionType.EXPONENTIAL);
        myClient.setSystem(mySystem);
        firstEvent.setClient(myClient);
        eventStack.addEvent(firstEvent);
        maxEvents = 100;
        while (!eventStack.isEmpty()) {
            eventProcessor.processEvent(eventStack.getNextEvent());
        }
        eventProcessor.printCounters();

    }
}