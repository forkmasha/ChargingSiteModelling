package eventSimulation;

import queueingSystem.Server;

public class EventProcessor {
    Event nextEvent;
    Server nextServer;
    static double deltaTime = 0;
    static int i, j, k, q = 0;

    public static void reset() {
        i = 0;
        j = 0;
        k = 0;
        q = 0;
        deltaTime = 0;
    }

    public void processEvent(Event event) {
        deltaTime = event.getExecTime() - EventSimulation.getCurrentTime();
        EventSimulation.setCurrentTime(event.getExecTime());

        if (event.getClient() != null) {
            switch (event.getEventType()) {
                case ARRIVAL:
                    i++;
                    event.getClient().getSystem().processArrival(event);
                    break;
                case DEPARTURE:
                    j++;
                    event.getClient().getSystem().processDeparture(event);
                    break;
                case QUEUEING:
                    q++;
                    event.getClient().getSystem().processQueueing(event);
                    break;
                case BLOCKING:
                    k++;
                    EventSimulation.eventStack.removeEvent(event);
                    break;
            }
        } else {
            System.out.println("Warning: Events' client is null");
        }
        //System.out.println("delta-Time(" + i + "/" + j + "/" + q + "/" + k + "): " + deltaTime);
    }

    public void printCounters() {
        System.out.println("EventCounters: " + i + "/" + j + "/" + q + "/" + k);
    }
}