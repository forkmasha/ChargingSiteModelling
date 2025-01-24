package eventSimulation;

import queueingSystem.Client;
import chargingSite.Simulation;


public class EventSimulation {
    private static int maxEvents = 100000;
    private static int numberOfEvents = 0;
    private static double currentTime = 0;

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
    public static double getBlockingRate(){
        return ((double)EventProcessor.blockingCounter)/EventProcessor.arrivalCounter;
    }

    public static EventProcessor eventProcessor = new EventProcessor();
    public static EventStack eventStack = new EventStack();

    public static void run(Client[] myClients, Simulation mySim) {
        EventSimulation.reset();
        Event.resetEventCounter();
        for(Client myClient : myClients) {
            new Event(0.0, EventType.ARRIVAL, myClient);
            numberOfEvents++;
        }
        new Event(EventProcessor.tick, EventType.CLOCK, mySim);
        numberOfEvents++;
        while (!eventStack.isEmpty()) {
            eventProcessor.processEvent(eventStack.getNextEvent());
        }
        System.out.print(";\n");
        // assuming all clients belong to the same system!
        myClients[0].getSystem().setBlockingRate(getBlockingRate());
        eventProcessor.printCounters();
    }
}