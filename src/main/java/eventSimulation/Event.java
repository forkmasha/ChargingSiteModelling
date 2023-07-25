package eventSimulation;

import queueingSystem.Client;

public class Event {
    private double execTime;
    private Client client;
    private EventType eventType;

    protected Event(double time) {
        this.execTime = time;
    }

    public double getExecTime() {
        return execTime;
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
}