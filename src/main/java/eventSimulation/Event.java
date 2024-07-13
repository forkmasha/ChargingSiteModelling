package eventSimulation;

import queueingSystem.Client;

public class Event {
    private static int eventCounter = 0;
    private static final int NumberOfEventsLimit = 10000000;
    private final int myIndex;
    private final double execTime;
    private final Client client;
    private final EventType eventType;

    public Event(double time, EventType type, Client client) {
        this.execTime = time;
        myIndex = eventCounter++;
        EventSimulation.eventStack.addEvent(this);
        if (eventCounter > NumberOfEventsLimit) {
            System.out.println("Number of generated events exceeds "+NumberOfEventsLimit);
            System.exit(-100);
        }
        this.eventType = type;
        this.client = client;
        if (client == null) System.out.println("Error: no client for " + type.name() + " event " + eventCounter + " at time " + time + " !");
    }
    public Event(double time, EventType type) {
        this.execTime = time;
        myIndex = eventCounter++;
        // myIndex = -1; // no 'real' event
        EventSimulation.eventStack.addEvent(this);
        this.eventType = type;
        this.client = null;
    }
    // public void setClient(Client client) {
    //    this.client = client;
    //}
    // public void setEventType(EventType eventType) {
    //    this.eventType = eventType;
    //}
    //public void setExecTime(double execTime) {
    //    this.execTime = execTime;
    //}
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
    public static void resetEventCounter() { eventCounter = 0; }
}