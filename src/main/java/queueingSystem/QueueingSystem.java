package queueingSystem;

import distributions.Distribution;
import distributions.DistributionType;
import eventSimulation.Event;
import eventSimulation.EventSimulation;
import eventSimulation.EventType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QueueingSystem {
    private int numberOfServers;
    private int occupiedServers = 0;
    private int queueSize;
    private double meanInterArrivalTime;
    private Distribution arrivalTimeDistribution;
    private DistributionType distributionType;
    private double meanTimeInSystem;
    private List<Server> servers = new ArrayList<>();

    private Queue myQueue;
    private Event nextEvent;
    private Server nextServer;
    private Client currentClient;
    private double currentTime;
    private List<Double> timesInQueue = new ArrayList<>();
    private List<Double> timesInSystem = new ArrayList<>();
    private List<Double> timesInService = new ArrayList<>();

    public List<Double> getTimesInQueue() {
        return timesInQueue;
    }

    public List<Double> getTimesInSystem() {
        return timesInSystem;
    }

    public List<Double> getTimesInService() {
        return timesInService;
    }

    public Queue getMyQueue() {
        return myQueue;
    }

    public void addServer(Server newServer) {
        servers.add(newServer);
        occupiedServers++;
    }

    public void removeServer(Server server) {
        servers.remove(server);
        occupiedServers--;
    }

    public Server getIdleServer() {
        //if (servers.isEmpty()) {
        //    return new Server();
        //} else if (servers.size() < numberOfServers) {
        if (occupiedServers < numberOfServers) {
            return new Server();
        } else {
            return null;
        }
    }

    public void setNumberOfServers(int numberOfServers) {
        this.numberOfServers = numberOfServers;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
        this.myQueue = new Queue(queueSize);
    }

    public void setDistributionType(DistributionType distributionType) {
        this.distributionType = distributionType;
        this.arrivalTimeDistribution = Distribution.create(distributionType);
    }

    public void setMeanInterArrivalTime(double meanInterArrivalTime) {
        this.meanInterArrivalTime = meanInterArrivalTime;
    }

    public double getMeanInterArrivalTime() {
        return meanInterArrivalTime;
    }

    public double getMeanTimeInSystem() {
        return meanTimeInSystem;
    }

    public Distribution getArrivalTimeDistribution() {
        return arrivalTimeDistribution;
    }

    public Server getServer(Client client) {
        //System.out.println("Error "+servers.size());
        if (!servers.isEmpty()) {
            //servers.sort(Comparator.comparingInt(Server::));
            for (Server i : servers) {
                if (i.servedClient == client) {
                    return i;
                }
            }
        }
        return null;
    }

    public void processArrival(Event arrival) {
        currentTime = arrival.getExecTime();
        currentClient = arrival.getClient();
        currentClient.setArrivalTime(currentTime);
        if (EventSimulation.getNumberOfEvents() < EventSimulation.getMaxEvents()) {
            nextEvent = new Event(currentTime +
                    this.getArrivalTimeDistribution().getSample(this.getMeanInterArrivalTime()));
            if (nextEvent.getExecTime() <= currentTime) {
                System.out.println("Warning: Zero InterArrivalTime: " + this.getArrivalTimeDistribution().
                        getSample(this.getMeanInterArrivalTime()));
                System.out.println("Warning: Zero Mean Inter-Arrival Time: " + this.getMeanInterArrivalTime());
            }
            nextEvent.setEventType(EventType.ARRIVAL);
            nextEvent.setClient(new Client(EventSimulation.meanServiceTime, DistributionType.UNIFORM, this));
            EventSimulation.eventStack.addEvent(nextEvent);
        }
        //event.setEventType(EventType.DEPARTURE);
        //nextServer = this.getIdleServer();
        if (occupiedServers < numberOfServers) {
            scheduleNextDeparture(arrival);
        } else {
            nextEvent = new Event(currentTime);
            nextEvent.setEventType(EventType.QUEUEING);
            nextEvent.setClient(currentClient);
            EventSimulation.eventStack.addEvent(nextEvent);
        }
    }
    public void processDeparture(Event departure) {
        currentTime = departure.getExecTime();
        currentClient = departure.getClient();
        currentClient.setTimeInSystem(currentTime);
        this.removeServer(this.getServer(currentClient));
        this.timesInSystem.add(currentClient.getTimeInSystem());
        this.timesInQueue.add(currentClient.getTimeInQueue());  // includes zero queueing times
        this.timesInService.add(currentClient.getTimeInSystem()-currentClient.getTimeInQueue());
        //this.servers.remove(currentClient);
        if (this.getMyQueue().getOccupation() > 0) {
            scheduleNextDeparture(departure);
            this.getMyQueue().removeClient(currentClient);
        }
        EventSimulation.eventStack.removeEvent(departure);
    }

    public void processQueueing(Event goInQueue) {
        currentTime = goInQueue.getExecTime();
        currentClient = goInQueue.getClient();
        if (this.getMyQueue().getOccupation() < this.getMyQueue().getSize()) {
            this.getMyQueue().addClientToQueue(currentClient);
        } else {
            nextEvent=new Event(currentTime);
            nextEvent.setEventType(EventType.BLOCKING);
            nextEvent.setClient(currentClient);
            EventSimulation.eventStack.addEvent(nextEvent);
        }
        EventSimulation.eventStack.removeEvent(goInQueue);
    }

    public void scheduleNextDeparture(Event currentEvent){
        currentTime = currentEvent.getExecTime();
        currentClient = currentEvent.getClient();
        currentClient.setTimeInQueue(currentTime);
        nextServer = getIdleServer();
        if(nextServer == null){
            System.out.println("NO Server available: " + servers.size());
            return;
        }
        nextEvent = new Event(currentTime +
                currentClient.getServiceTimeDistribution().
                        getSample(currentClient.getMeanServiceTime()));
        if (nextEvent.getExecTime() <= currentTime) {
            System.out.println("Warning: Zero Service Time: " + currentClient.getServiceTimeDistribution().
                    getSample(currentClient.getMeanServiceTime()));
            System.out.println("Warning: Zero Mean Service Time: " + currentClient.getMeanServiceTime());
        }
        nextEvent.setEventType(EventType.DEPARTURE);
        nextEvent.setClient(currentClient);
        EventSimulation.eventStack.addEvent(nextEvent);
        this.addServer(nextServer);
    }


   /* private Event createNextArrival(){
        Event newEvent = new Event(EventSimulation.getTime()+arrivalTimeDistribution.getSample());
         newEvent.setClient(new Client());
    }

    private void processArrivalEvent(Event event) {
        if (event.eventType!= EventType.ARRIVAL){
            System.out.println("Error. Arrival is not of arrivalType");
        }

        if ( < maxCars) {
            double nextArrivalTime = eventTime + exponentialDistribution(meanArrivalInterval);
            eventStack.addEvent(new ArrivalEvent(nextArrivalTime));
        } else {
            return; // Повертаємося, якщо кількість автомобілів досягає максимального значення
        }
        // Збільшуємо лічильник обслужених автомобілі
        i++;
        if (numServers > servicedCars.size()) {   // directly enter servive
            double serviceTime = calculateServiceTime();
            eventStack.addEvent(new DepartureEvent(eventTime + serviceTime));
            servicedCars.add(new Car(eventTime, 0.0, serviceTime));
        } else if (queueStartTimes.size() < queueLength) {   // enter waiting queue
            queueStartTimes.add(eventTime);
            // Зберігаємо час початку очікування в черзі для автомобіля
        } else {  // arrival is blocked (deflected)
            k++;
        }
        arrivalRateList.add(1.0 / meanArrivalInterval); // Calculate arrival rate from mean arrival interval
    }*/
}
