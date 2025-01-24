package eventSimulation;

import chargingSite.ElectricVehicle;
import queueingSystem.Server;

import static eventSimulation.EventType.CLOCK;

public class EventProcessor {
    Event nextEvent;
    Server nextServer;
    ElectricVehicle chargedVehicle;
    static double timeScale = 1.0;
    static double tick = 1.0 / 60;
    static double deltaTime = 0;
    static int arrivalCounter, departureCounter, blockingCounter, queueingCounter, clockCounter = 0;
    double timeSinceLastRealEvent = 0;

    public static void reset() {
        arrivalCounter = 0;
        departureCounter = 0;
        blockingCounter = 0;
        queueingCounter = 0;
        clockCounter = 0;
        deltaTime = 0;
    }

    public void processEvent(Event event) {
        deltaTime = event.getExecTime() - EventSimulation.getCurrentTime();
        if (deltaTime >= 0) {
            EventSimulation.setCurrentTime(event.getExecTime());
        } else {
            System.out.println("Warning: negative time between events " + deltaTime + " occurred and is reset to zero!");
            deltaTime = 0;
        }
        //reset DeltaTime to time since last 'real' event execution
        if(event.getEventType() == CLOCK) {
            timeSinceLastRealEvent += deltaTime;
        } else {
            deltaTime += timeSinceLastRealEvent;
            timeSinceLastRealEvent = 0;
            timeScale = event.getClient().getSystem().getMeanInterArrivalTime();
        }

        if (event.getClient() != null) {
            for (int i=0; i<event.getClient().getSystem().getServers().size();i++) {
                Server next = event.getClient().getSystem().getServers().get(i);
                if (next != null ) next.getClient().processClient(deltaTime);
            }

            switch (event.getEventType()) {
                case ARRIVAL:
                    arrivalCounter++;
                    event.getClient().getSystem().processArrival(event);
                    break;
                case DEPARTURE:
                    departureCounter++;
                    event.getClient().getSystem().processDeparture(event);
                    break;
                case QUEUEING:
                    queueingCounter++;
                    event.getClient().getSystem().processQueueing(event);
                    break;
                case BLOCKING:
                    blockingCounter++;
                    // do nothing -> the client is deflected into nirvana
                    break;
                default:
                    System.out.println("ERROR: Unknown EventType cannot be handled!");
            }
        } else {
            switch (event.getEventType()) {
                case CLOCK:
                    clockCounter++;
                    if(EventSimulation.eventStack.events.size() > 1) { // there is at least one real event
                        nextEvent = new Event(EventSimulation.getCurrentTime() + tick, CLOCK, event.getSimulation());
                    }
                    event.getSimulation().executeClockTick(timeScale);
                    break;
                case INTERRUPT:
                    System.out.println("An INTERRUPT occurred!");
                    System.exit(-200);
                    break;
                default:
                    System.out.println("ERROR: Unknown EventType cannot be handled!L");
            }
        }
        if(arrivalCounter % 1000 == 0){
            System.out.print(".");
        }
        EventSimulation.eventStack.removeEvent(event);
    }

    public void printCounters() {
        System.out.print("EventCounters: " + arrivalCounter + "/" + departureCounter + "/" + queueingCounter +
                "/" + blockingCounter + "/" + clockCounter );
        System.out.println("\t BlockingRate: " + ((double) blockingCounter) / arrivalCounter);
    }
}