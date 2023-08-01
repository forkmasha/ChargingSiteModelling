package eventSimulation;

import queueingSystem.Client;

public class Event {
    private double execTime;
    private static int eventCounter = 0;
    private final int myIndex;
    private Client client;
    private EventType eventType;

    public Event(double time) {
        this.execTime = time;
        myIndex = eventCounter++;
        EventSimulation.eventStack.addEvent(this);
        if (eventCounter > 10000000) {
            System.out.println("Number of generated events exceeds 10.000.000");
            System.exit(-100);
        }
    }

    public double getExecTime() {
        return execTime;
    }
    public int getIndex() {
        return myIndex;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public EventType getEventType() {
        return eventType;
    }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    public Client getClient() {
        return client;
    }
    public void setExecTime(double execTime) {
        this.execTime = execTime;
    }
    public static void resetEventCounter() { eventCounter = 0; }
}