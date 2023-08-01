package queueingSystem;

import distributions.Distribution;
import distributions.DistributionType;
import eventSimulation.Event;
import eventSimulation.EventSimulation;
import eventSimulation.EventType;
import results.Monitor;

import java.util.ArrayList;
import java.util.List;

public class QueueingSystem {
    private String systemName;
    private int numberOfServers;
    private int occupiedServers = 0;
    private int queueSize;
    private double blockingRate = 0;
    private double meanInterArrivalTime;
    private Distribution arrivalTimeDistribution;
    private DistributionType distributionType;
    private double meanTimeInSystem;
    private ArrayList<Server> servers = new ArrayList<>();

    private Queue myQueue;
    private Event nextEvent;
    private Server nextServer;
    private Client currentClient;
    private Client nextClient;
    private double currentTime;
    private List<Double> timesInQueue = new ArrayList<>();
    private List<Double> timesInSystem = new ArrayList<>();
    private List<Double> timesInService = new ArrayList<>();
    private Monitor blockingRates = new Monitor();

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

    public double getBlockingRate() {
        return blockingRate;
    }

    public QueueingSystem(String name) {
        this.systemName = name;
    }

    public void setBlockingRate(double blockingRate) {
        this.blockingRate = blockingRate;
    }

    public void addServer(Server newServer) {
        servers.add(newServer);
        occupiedServers++;
    }

    public void removeServer() {
        servers.remove(0);
        //servers.remove(server); // somehow that does not remove the server from the list...
        occupiedServers--;
    }
    public void removeServer(Client client) {
        System.out.println("ERROR: This method seems not to work!");
        servers.remove(getServer(client));
        //servers.remove(0);
        occupiedServers--;
    }
    public void removeServer(Server idle) {
        System.out.println("ERROR: This method seems not to work!");
        servers.remove(idle);
        //servers.remove(0);
        occupiedServers--;
    }

    public int getNumberOfServersInUse() {
        return this.servers.size();
    }

    public Server getIdleServer() {
        Server newServer;
        //if (servers.isEmpty()) {
        //    return new Server();
        //} else if (servers.size() < numberOfServers) {
        if (occupiedServers < numberOfServers) {
            newServer = new Server();
            servers.add(newServer);
            occupiedServers++;
            return newServer;
        } else {
            return null;
        }
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


    public void processArrival(Event arrival) {
        currentTime = arrival.getExecTime();
        currentClient = arrival.getClient();
        currentClient.setArrivalTime(currentTime);
        if (EventSimulation.getNumberOfEvents() < EventSimulation.getMaxEvents()) {
            EventSimulation.incNumberOfEvents();
            nextEvent = new Event(currentTime +
                    this.getArrivalTimeDistribution().getSample(this.getMeanInterArrivalTime()));
            nextEvent.setEventType(EventType.ARRIVAL);
            nextClient = new Client(  // schedule the arrival of the next client with the same properties the currently arriving client has
                    currentClient.getMeanServiceTime(),
                    currentClient.getServiceTimeDistribution().getType(),
                    currentClient.getSystem()
            );
            nextEvent.setClient(nextClient);
            if (nextEvent.getExecTime() <= currentTime) {
                System.out.println("Warning: Zero InterArrivalTime: " + this.getArrivalTimeDistribution().
                        getSample(this.getMeanInterArrivalTime()));
                System.out.println("Warning: Zero Mean Inter-Arrival Time: " + this.getMeanInterArrivalTime());
            }
            //if (nextClient.getTimeInQueue()>0) { System.out.println("Error: A new Client with TimeInQueue > zero is initialised."); }
        }
        //event.setEventType(EventType.DEPARTURE);
        //nextServer = this.getIdleServer();
        if (getNumberOfServersInUse() < numberOfServers) {
            scheduleNextDeparture(currentTime, currentClient);
        } else {
            currentClient.setTimeInQueue(0.0);
            nextEvent = new Event(currentTime);
            nextEvent.setEventType(EventType.QUEUEING);
            nextEvent.setClient(currentClient);
            EventSimulation.eventProcessor.processEvent(nextEvent); // process instantly
        }
    }

    public void processDeparture(Event departure) {
        currentTime = departure.getExecTime();
        currentClient = departure.getClient();
        double timeInSystem = currentTime - currentClient.getArrivalTime();
        double timeInQueue = currentClient.getTimeInQueue();
        currentClient.setTimeInSystem(timeInSystem);
        this.removeServer();
        //this.removeServer(currentClient); // does not do it?
        this.timesInSystem.add(timeInSystem);
        if (timeInQueue > 0.0) {
            this.timesInQueue.add(timeInQueue);
            this.timesInService.add(timeInSystem - timeInQueue);
            if (timeInQueue > 2 * myQueue.getSize()/(numberOfServers/currentClient.getMeanServiceTime())) {
                System.out.println("Warning: Time in queue "+ timeInQueue +" is exceptionally long for "
                        + myQueue.getOccupation() +" clients waiting and a total service rate of "
                        + numberOfServers/currentClient.getMeanServiceTime() +" clients per time unit!");
            }
        } else {
            this.timesInQueue.add(0.0);  // use to include zero queueing times
            this.timesInService.add(timeInSystem);
        }
        //this.timesInService.add(currentClient.getTimeInSystem()-currentClient.getTimeInQueue());
        if (this.getMyQueue().getOccupation() > 0) {
            nextClient = this.getMyQueue().getNextClient();
            nextClient.setTimeInQueue(currentTime - nextClient.getArrivalTime());
            scheduleNextDeparture(currentTime, nextClient);
        }
    }

    public void processQueueing(Event goInQueue) {
        currentTime = goInQueue.getExecTime();
        currentClient = goInQueue.getClient();
        if (this.getMyQueue().getOccupation() < this.getMyQueue().getSize()) {
            this.getMyQueue().addClientToQueue(currentClient);
            //currentClient.setTimeInQueue(0.0);
            //currentClient.setArrivalTime(currentTime);
        } else {
            nextEvent = new Event(currentTime);
            nextEvent.setEventType(EventType.BLOCKING);
            nextEvent.setClient(currentClient);
            EventSimulation.eventProcessor.processEvent(nextEvent); // process instantly
        }
    }

    public void scheduleNextDeparture(double currentTime, Client currentClient) {
        currentClient.setTimeInQueue(currentTime - currentClient.getArrivalTime());
        nextServer = getIdleServer();
        if (nextServer == null) {
            System.out.println("NO Server available: " + servers.size());
            return;
        }
        nextEvent = new Event(currentTime + currentClient.getServiceTimeDistribution().getSample(currentClient.getMeanServiceTime()));
        if (nextEvent.getExecTime() <= currentTime) {
            System.out.println("Warning: Zero Service Time: " +
                    currentClient.getServiceTimeDistribution().getSample(currentClient.getMeanServiceTime()));
            System.out.println("Warning: Zero Mean Service Time: " + currentClient.getMeanServiceTime());
        }
        nextEvent.setEventType(EventType.DEPARTURE);
        nextEvent.setClient(currentClient);
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
