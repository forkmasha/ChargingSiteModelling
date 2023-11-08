package queueingSystem;

import chargingSite.ChargingSite;
import chargingSite.Simulation;
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

    private ChargingSite site;
    private int numberOfServers;
    private int occupiedServers = 0;
    private int queueSize;
    private double blockingRate = 0;
    private double meanInterArrivalTime;
    private Distribution arrivalTimeDistribution;
    private DistributionType distributionType;
    private double meanTimeInSystem;
    private ArrayList<Server> servers = new ArrayList<>();

    private final Queue myQueue;
    private Event nextEvent;
    private Server nextServer;
    private Client currentClient;
    private Client nextClient;
    private double currentTime;
    private final List<Double> timesInQueue = new ArrayList<>();
    private final List<Double> timesInSystem = new ArrayList<>();
    private final List<Double> timesInService = new ArrayList<>();
    private final List<Double> energyCharged = new ArrayList<>();
    private final Monitor blockingRates = new Monitor();
    public List<Double> getTimesInQueue() {
        return timesInQueue;
    }

    public List<Double> getTimesInSystem() {
        return timesInSystem;
    }

    public List<Double> getTimesInService() {
        return timesInService;
    }

    private  List<Double> amountsCharged  = new ArrayList<>(); // (mean + conf., std, 10% and 90% quantiles )
    private  List<Double> sitePowers  = new ArrayList<>(); // power demand of entire charging site (mean + conf., std, max )

    private  List<Double> chargingDeviations  = new ArrayList<>(); // difference between demanded and actually charged energy (mean + conf., std)

    public List<Double> getSitePowers() {
        return sitePowers;
    }
    public List<Double> getAmountsCharged() {
        return amountsCharged;
    }
    public String getSystemName() {
        return systemName;
    }
    public Queue getMyQueue() {
        return myQueue;
    }
    public ChargingSite getChargingSite() {
        return this.site;
    }

    public double getBlockingRate() {
        return blockingRate;
    }

    public List<Double> getChargingDeviations() {
        return chargingDeviations;
    }

    public QueueingSystem(int numberOfServers, int queueSize, Queue.QueueingType queueingType) {
        this.myQueue = new Queue(queueSize,queueingType);
        this.numberOfServers = numberOfServers;
        this.site = new ChargingSite(numberOfServers, Simulation.MAX_SITE_POWER); // TO BE SET via GUI
        this.resetQueueingSystem();
    }

    public String getKendallName(Simulation mySim) {
        return Distribution.getTitleAbbreviation(String.valueOf(this.arrivalTimeDistribution.getType())) + "/"
                + Distribution.getTitleAbbreviation(String.valueOf(mySim.getSERVICE_TYPE())) + "/"
                + numberOfServers +"/" + numberOfServers + queueSize;
    }
    public int getNumberOfServers() {
        return numberOfServers;
    }

    /* this generator creates a non-functional (dummy) queueing system only - DO NOT USE!
        public QueueingSystem(String name) {
            this.systemName = name;
        }*/
    public void resetQueueingSystem() {
        timesInQueue.clear();
        timesInSystem.clear();
        timesInService.clear();
        amountsCharged.clear();
        sitePowers.clear();
        chargingDeviations.clear();
        blockingRates.values.clear();
        Client.resetClientCounter();
    }

    public void setName(String name) { this.systemName = name; }

    public void setBlockingRate(double blockingRate) {
        this.blockingRate = blockingRate;
    }

    public void addServer(Server newServer) {
        servers.add(newServer);
        occupiedServers++;
    }

    public void removeServer() {
        servers.remove(0); //removes the first (longest occupied) server
        occupiedServers--;
    }

    public void removeServer(Client client) {
        servers.remove(getServer(client));
        occupiedServers--;
    }
    public void removeServer(Server idle) {
        servers.remove(idle);
        occupiedServers--;
    }

    public double getTotalPower() {
        double totalPower = 0;
        for (Server nextServer : servers) {
            totalPower += nextServer.getClient().getCar().getChargingPower();
        }
        if(totalPower > site.getMaxSitePower()) {
            for (Server nextServer : servers) {
                nextServer.getClient().getCar().updateChargingPower(totalPower);
            }
            totalPower = 0;
            for (Server nextServer : servers) {
                totalPower += nextServer.getClient().getCar().getChargingPower();
            }
            if (totalPower>site.getMaxSitePower()) {
                System.out.println("ERROR in getTotalPower: Site power " + totalPower + "is bigger than maximum possible " + site.getMaxSitePower() + " !");
            }
        }
        return totalPower;
    }

    public int getNumberOfServersInUse() {
        return this.servers.size();
    }

    public Server getIdleServer() {
        Server newServer;
        if (occupiedServers < numberOfServers) {
            newServer = new Server();
            servers.add(newServer);
            occupiedServers++;
            return newServer;
        } else {
            return null;
        }
    }
    public Server getServer(int index) {   // index works :)
        int n = servers.size();
        if (n > 0 && index < n) {
            return servers.get(index);
        } else {
            return (null);
        }
    }
    public Server getServer(Client client) {
        //System.out.println("Error "+servers.size());
        if (!servers.isEmpty()) {
            //servers.sort(Comparator.comparingInt(Server::));
            for (Server s : servers) {
                if (s.servedClient.equals(client)) {
                    return s;
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
        //this.myQueue = new Queue(queueSize);
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
        currentClient.getCar().resetEnergyCharged();

        //    double currentChargingPower = currentClient.getCar().getChargingPower();
        //  double updatedChargingPower = currentChargingPower * occupiedServers;
        //  currentClient.getCar().setChargingPower(updatedChargingPower);
        //  currentClient.getCar().getChargingPowerHistory().add(updatedChargingPower);


        if (EventSimulation.getNumberOfEvents() < EventSimulation.getMaxEvents()) {
            EventSimulation.incNumberOfEvents();
            nextEvent = new Event(currentTime +
                    this.getArrivalTimeDistribution().getSample(this.getMeanInterArrivalTime()));
            nextEvent.setEventType(EventType.ARRIVAL);
            nextClient = new Client(  // schedule the arrival of the next client with the same properties the currently arriving client has
                    nextEvent.getExecTime(),
                    currentClient.getMeanServiceTime(),
                    currentClient.getServiceTimeDistribution().getType(),
                    currentClient.getSystem()
            );
            nextEvent.setClient(nextClient);
            /*if (nextEvent.getExecTime() <= currentTime) {
                System.out.println("Warning: Zero InterArrivalTime: " + this.getArrivalTimeDistribution().
                        getSample(this.getMeanInterArrivalTime()));
                System.out.println("Warning: Zero Mean Inter-Arrival Time: " + this.getMeanInterArrivalTime());
            }*/
            //if (nextClient.getTimeInQueue()>0) { System.out.println("Error: A new Client with TimeInQueue > zero is initialised."); }
        }
        if (getNumberOfServersInUse() < numberOfServers) {
            scheduleNextDeparture(currentTime, currentClient);
        } else {
            //currentClient.setTimeInQueue(0.0);
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
        //this.removeServer(getServer(currentClient)); // also works, below is 'easier'
        this.removeServer(currentClient);
        this.timesInSystem.add(timeInSystem);
        if (timeInQueue > 0.0) {
            //this.timesInQueue.add(timeInQueue);
            // this.timesInService.add(timeInSystem - timeInQueue);

            // -----------this.powerCharged.add((timeInSystem - timeInQueue)*currentClient.getCar().getChargingPower());
            // use in case queueing time appears to be too big to get some info
            /*if (timeInQueue > 2 * myQueue.getSize()/(numberOfServers/currentClient.getMeanServiceTime())) {
                System.out.println("Warning: Time in queue "+ timeInQueue +" is exceptionally long for "
                        + myQueue.getOccupation() +" clients waiting and a total service rate of "
                        + numberOfServers/currentClient.getMeanServiceTime() +" clients per time unit!");
            }*/
        } else {
            timeInQueue=0.0;//to consider 0 queueing time in statistics

            // -----------this.powerCharged.add(timeInSystem *currentClient.getCar().getChargingPower());
        }
        double timeInService = timeInSystem-timeInQueue;
        this.timesInQueue.add(timeInQueue);
        this.timesInService.add(timeInSystem-timeInQueue);

        //this.amountsCharged.add(timeInService * currentClient.getCar().getMaxPower());
        this.amountsCharged.add(currentClient.getCar().getEnergyCharged());
        this.chargingDeviations.add(currentClient.getCar().getChargeDemand()-currentClient.getCar().getEnergyCharged());

        if (myQueue.getOccupation() > 0) {
            nextClient = myQueue.pullNextClientFromQueue(currentTime);
            if (nextClient.getTimeInQueue() <= 0.0) {
                System.out.println("Error: A Client with TimeInQueue = " + nextClient.getTimeInQueue() + " has been taken from the queue!");
            }
            scheduleNextDeparture(currentTime, nextClient);
        }

    }

    public void processQueueing(Event goInQueue) {
        currentTime = goInQueue.getExecTime();
        currentClient = goInQueue.getClient();
        if (myQueue.getOccupation() < this.getMyQueue().getSize()) {
            myQueue.addClientToQueue(currentClient);
            //currentClient.setTimeInQueue(0.0);
            //currentClient.setArrivalTime(currentTime);
        } else {
            nextEvent = new Event(currentTime);
            nextEvent.setEventType(EventType.BLOCKING);
            nextEvent.setClient(currentClient);
            EventSimulation.eventProcessor.processEvent(nextEvent); // process instantly
        }
    }
    public void instantDeparture(Client currentClient) {
        nextEvent = new Event(EventSimulation.getCurrentTime());
        nextEvent.setEventType(EventType.DEPARTURE);

        // used to track down the negative time in system issue
        if(currentTime<=currentClient.getArrivalTime()) {
            System.out.println("current Time: " + currentTime
                    + " >? arrival Time: " + currentClient.getArrivalTime());
            // this.removeServer(currentClient);
        }

        currentClient.setTimeInService(currentTime - (currentClient.getArrivalTime() + currentClient.getTimeInQueue()));
        nextEvent.setClient(currentClient);
        EventSimulation.eventProcessor.processEvent(nextEvent); // process instantly
    }

    public void scheduleNextDeparture(double currentTime, Client currentClient) {
        nextServer = getIdleServer();
        if (nextServer == null) {
            System.out.println("Error: Cannot schedule next Departure - NO Server available: " + servers.size());
            return;
        }
        nextServer.setClient(currentClient);
        currentClient.getCar().setChargingPoint(this.getChargingSite().getChargingPoint(servers.indexOf(nextServer)));
        currentClient.getCar().setMyServer(nextServer);

        /*double currentChargingPower = currentClient.getCar().getChargingPower();
        double updatedChargingPower = currentChargingPower * occupiedServers;
        currentClient.getCar().setChargingPower(updatedChargingPower);
        currentClient.getCar().getChargingPowerHistory().add(updatedChargingPower);*/

        if (currentClient.getMeanServiceTime()>0) {
            nextEvent = new Event(currentTime + currentClient.getServiceTimeDistribution().getSample(currentClient.getMeanServiceTime()));
            //if (nextEvent.getExecTime() <= currentTime) { System.out.println("Error: Next departure cannot be in the past!"); }
            nextEvent.setEventType(EventType.DEPARTURE);
            currentClient.setTimeInService(nextEvent.getExecTime() - currentTime);
            nextEvent.setClient(currentClient);
        }
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