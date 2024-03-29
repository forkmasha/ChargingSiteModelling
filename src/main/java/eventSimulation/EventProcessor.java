package eventSimulation;

import chargingSite.ElectricVehicle;
import queueingSystem.Server;

public class EventProcessor {
    Event nextEvent;
    Server nextServer;
    ElectricVehicle chargedVehicle;
    double tick = 0.05;
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
        if (deltaTime>=0) {
            EventSimulation.setCurrentTime(event.getExecTime());
        } else {
            System.out.println("Warning: negative time between events " + deltaTime + " occurred and is reset to zero!");
            deltaTime = 0;
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
            System.out.println("Warning: Events' client is NULL");
        }
        if(i % 1000 == 0){
            System.out.print("."); //System.exit(1);
        }
        //System.out.println("EventStackSize: " + EventSimulation.eventStack.events.size());
        EventSimulation.eventStack.removeEvent(event);
        //System.out.println("delta-Time(" + i + "/" + j + "/" + q + "/" + k + "): " + deltaTime);
    }

    public void printCounters() {
        System.out.print("EventCounters: " + i + "/" + j + "/" + q + "/" + k);
        System.out.println("\t BlockingRate: " + ((double) k)/i);
    }
}