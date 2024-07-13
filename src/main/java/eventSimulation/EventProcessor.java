package eventSimulation;

import chargingSite.ElectricVehicle;
import queueingSystem.Server;

import static eventSimulation.EventType.CLOCK;

public class EventProcessor {
    Event nextEvent;
    Server nextServer;
    ElectricVehicle chargedVehicle;
    static double tick = 1.0/60;
    static double deltaTime = 0;
    static int i, j, k, q, t = 0;
    double timeSinceLastRealEvent = 0;

    public static void reset() {
        i = 0;
        j = 0;
        k = 0;
        q = 0;
        t = 0;
        deltaTime = 0;
    }

    public void processEvent(Event event) {
        deltaTime = event.getExecTime() - EventSimulation.getCurrentTime();
        if (deltaTime>=0) {
            EventSimulation.setCurrentTime(event.getExecTime());
        } else {
            System.out.println("Warning: negative time between events " + deltaTime + " occurred and is reset to zero!");
            deltaTime = 0;
        }
        //reset DeltaTime to time since last 'real' event execution
        if(event.getEventType()==CLOCK) {
            timeSinceLastRealEvent += deltaTime;
        } else {
            deltaTime += timeSinceLastRealEvent;
            timeSinceLastRealEvent = 0;
        }

        if (event.getClient() != null) {
            //for (Server next : event.getClient().getSystem().getServers()) {
            for (int i=0; i<event.getClient().getSystem().getServers().size();i++) {
                Server next = event.getClient().getSystem().getServers().get(i);
                if (next != null ) next.getClient().processClient(deltaTime);
            }
            /*
            n = 0;
            nextServer = event.getClient().getSystem().getServer(n);
            while (nextServer != null) {
                nextServer.getClient().processClient(deltaTime,event.getClient().getSystem().getTotalPower());
                nextServer = event.getClient().getSystem().getServer(++n);
            }
            */

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
                    // do nothing -> the client is deflected into nirvana
                    break;
                default:
                    System.out.println("ERROR: Unknown EventType cannot be handled!");
            }
        } else {
            switch (event.getEventType()) {
                case CLOCK:
                    t++;
                    if(EventSimulation.eventStack.events.size() > 1) { // there is at least one real event
                        nextEvent = new Event(EventSimulation.getCurrentTime() + tick, CLOCK);
                    }
                    //do something -> record system states...

                    break;
                case INTERRUPT:
                    System.out.println("An INTERRUPT occurred!");
                    System.exit(-200);
                    break;
                default:
                    System.out.println("ERROR: Unknown EventType cannot be handled!L");
            }
        }
        if(i % 1000 == 0){
            System.out.print("."); //System.exit(1);
        }
        //System.out.println("EventStackSize: " + EventSimulation.eventStack.events.size());
        EventSimulation.eventStack.removeEvent(event);
        //System.out.println("delta-Time(" + i + "/" + j + "/" + q + "/" + k + "): " + deltaTime);
    }

    public void printCounters() {
        System.out.print("EventCounters: " + i + "/" + j + "/" + q + "/" + k + "/" + t );
        System.out.println("\t BlockingRate: " + ((double) k)/i);
    }
}