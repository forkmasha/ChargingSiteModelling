package eventSimulation;

import queueingSystem.Client;

public class Event {
    private double execTime;
    private static int eventCounter = 0;
    private final int myIndex;
    private Client client;
    private EventType eventType;

    public Event(double time, EventType type, Client client) {
        this.execTime = time;
        myIndex = eventCounter++;
        EventSimulation.eventStack.addEvent(this);
        if (eventCounter > 10000000) {
            System.out.println("Number of generated events exceeds 10.000.000");
            System.exit(-100);
        }
        this.eventType = type;
        this.client = client;
        if (client == null) System.out.println("Error: no client for " + type.name() + " event " + eventCounter + " at time " + time + " !");
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