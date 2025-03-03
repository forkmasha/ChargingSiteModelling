package eventSimulation;

import chargingSite.Simulation;
import queueingSystem.Client;

public class Event {
    private static int eventCounter = 0;
    private static final int NumberOfEventsLimit = 10000000;
    private final int myIndex;
    private final double execTime;
    private final Client client;
    private final Simulation simulation;
    private final EventType eventType;

    public Event(double time, EventType type, Client client) {
        this.execTime = time;
        myIndex = eventCounter++;
        EventSimulation.eventStack.addEvent(this);
        if (eventCounter > NumberOfEventsLimit) {
            System.out.println("Number of generated events exceeds "+NumberOfEventsLimit);
            System.exit(-100);
        }
        this.simulation = null;
        this.eventType = type;
        this.client = client;
        if (client == null) System.out.println("Error: no client for " + type.name() + " event " + eventCounter + " at time " + time + " !");
    }
    public Event(double time, EventType type, Simulation simulation) {
        this.execTime = time;
        myIndex = eventCounter++;
        EventSimulation.eventStack.addEvent(this);
        this.simulation = simulation;
        this.eventType = type;
        this.client = null;
    }
    public double getExecTime() {
        return execTime;
    }
    public int getIndex() {
        return myIndex;
    }
    public EventType getEventType() {
        return eventType;
    }

    public Client getClient() {
        return client;
    }
    public Simulation getSimulation() {return simulation; }
    public static void resetEventCounter() { eventCounter = 0; }
}