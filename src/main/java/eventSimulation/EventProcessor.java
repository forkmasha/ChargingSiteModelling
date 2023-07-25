package eventSimulation;

public class EventProcessor {
    Event nextEvent;
    double deltaTime = 0;
    int i, j, k = 0;


    public void processEvent(Event event) {
        deltaTime = event.getExecTime() - EventSimulation.getCurrentTime();
        System.out.println("\n delta-Time: " + deltaTime);
        EventSimulation.setCurrentTime(event.getExecTime());
        switch (event.getEventType()) {
            case ARRIVAL:
                i++;
                if (EventSimulation.getNumberOfEvents() < EventSimulation.getMaxEvents()) {
                    nextEvent = new Event(event.getExecTime()
                            + event.getClient().getSystem().getArrivalTimeDistribution().
                            getSample(event.getClient().getSystem().getMeanInterArrivalTime()));
                    nextEvent.setEventType(EventType.ARRIVAL);
                    EventSimulation.eventStack.addEvent(nextEvent);
                }
                event.setEventType(EventType.DEPARTURE);
                if (true) {
                    nextEvent = new Event(event.getExecTime()
                            + event.getClient().getServiceTimeDistribution().
                            getSample(event.getClient().getMeanServiceTime()));
                    nextEvent.setEventType(EventType.DEPARTURE);
                    EventSimulation.eventStack.addEvent(nextEvent);
                }
                break;
            case DEPARTURE:
                j++;
                EventSimulation.eventStack.removeEvent(event);
                break;
            case QUEUEING:
                break;
            case BLOCKING:
                k++;
                EventSimulation.eventStack.removeEvent(event);
                break;
        }
    }
    public void printCounters(){
        System.out.println("EventCounters: " + i + "/" + j+"/"+k);

    }
}