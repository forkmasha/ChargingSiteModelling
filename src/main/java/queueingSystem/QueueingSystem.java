package queueingSystem;

import chargingSite.ChargingSite;
import chargingSite.ElectricVehicle;
import chargingSite.Simulation;
import distributions.Distribution;
import distributions.DistributionType;
import eventSimulation.Event;
import eventSimulation.EventSimulation;
import eventSimulation.EventType;
import simulationParameters.SimulationParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class QueueingSystem {
    private static final Logger logger = Logger.getLogger(QueueingSystem.class.getName());
    private String systemName;
    private int numberOfServers;
    private int occupiedServers = 0;
    private int queueSize;
    private double blockingRate = 0;
    private double meanInterArrivalTime;
    private ChargingSite site;
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

    public List<Double> getTimesInQueue() {
        return timesInQueue;
    }

    public List<Double> getTimesInSystem() {
        return timesInSystem;
    }

    public List<Double> getTimesInService() {
        return timesInService;
    }

    private List<Double> amountsCharged = new ArrayList<>(); // (mean + conf., std, 10% and 90% quantiles )
    private List<Double> sitePowers = new ArrayList<>(); // power demand of entire charging site (mean + conf., std, max )

    private List<Double> chargingDeviations = new ArrayList<>(); // difference between demanded and actually charged energy (mean + conf., std)

    public List<Double> getSitePowers() {
        return sitePowers;
    }

    public List<Double> getAmountsCharged() {
        return amountsCharged;
    }

    public QueueingSystem(SimulationParameters parameters) {
        this.myQueue = new Queue(parameters.getQUEUE_SIZE(), parameters.getQUEUEING_TYPE());
        this.numberOfServers = parameters.getNUMBER_OF_SERVERS();
        this.site = new ChargingSite(parameters); // TO BE SET via GUI
        this.resetQueueingSystem();
    }


    public void setName(String name) {
        this.systemName = name;
    }

    public void setBlockingRate(double blockingRate) {
        this.blockingRate = blockingRate;
    }

    public void setDistributionType(DistributionType distributionType) {
        this.distributionType = distributionType;
        this.arrivalTimeDistribution = Distribution.create(distributionType);
    }

    public void setMeanInterArrivalTime(double meanInterArrivalTime) {
        this.meanInterArrivalTime = meanInterArrivalTime;
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

    public List<Double> getChargingDeviations() {
        return chargingDeviations;
    }

    public int getNumberOfServers() {
        return numberOfServers;
    }

    public ArrayList<Server> getServers() {
        return servers;
    }

    public double getMeanInterArrivalTime() {
        return meanInterArrivalTime;
    }

    public Distribution getArrivalTimeDistribution() {
        return arrivalTimeDistribution;
    }
    public Double getBlockingRate() {return blockingRate;}
    public Double getEquilibriumArrivalRate() {
        return numberOfServers / meanInterArrivalTime;
    }

    public String getKendallName(Simulation mySim) {
        return Distribution.getTitleAbbreviation(String.valueOf(this.arrivalTimeDistribution.getType())) + "/"
                + Distribution.getTitleAbbreviation(String.valueOf(mySim.getParameters().getServiceType())) + "/"
                + numberOfServers + "/" + numberOfServers + queueSize;
    }


    public void resetQueueingSystem() {
        timesInQueue.clear();
        timesInSystem.clear();
        timesInService.clear();
        amountsCharged.clear();
        sitePowers.clear();
        chargingDeviations.clear();
        site.resetPowerRecords();
        Client.resetClientCounter();
    }

    public void removeServer(Client client) {
        Server server = getServer(client);
        server.chargingPoint.unplugCar();
        servers.remove(server);
        occupiedServers--;
    }

    public int getNumberOfServersInUse() {
        return this.servers.size();
    }

    public Server getIdleServer(Client myClient) {
        Server newServer;
        if (occupiedServers < numberOfServers) {
            newServer = new Server(this, myClient);
            servers.add(newServer);
            occupiedServers++;
            return newServer;
        } else {
            return null;
        }
    }

    public Server getServer(Client client) {
        if (!servers.isEmpty()) {
            for (Server s : servers) {
                if (s.servedClient.equals(client)) {
                    return s;
                }
            }
        }
        return null;
    }

    public void processArrival(Event arrival) {
        currentTime = arrival.getExecTime();
        currentClient = arrival.getClient();
        currentClient.getCar().resetEnergyCharged();

        if (EventSimulation.getNumberOfEvents() < EventSimulation.getMaxEvents()) {
            EventSimulation.incNumberOfEvents();
            double newExecTime = currentTime + this.getArrivalTimeDistribution().getSample(this.getMeanInterArrivalTime());
            nextClient = new Client(  // create next client with the same properties the currently arriving client has
                    newExecTime,
                    ElectricVehicle.createRandomCar(currentClient.getCar().getSimParameters()),
                    currentClient.getSystem()
            );
            nextEvent = new Event( // schedule the arrival of the next client
                    newExecTime,
                    EventType.ARRIVAL,
                    nextClient
            );
        }
        if (getNumberOfServersInUse() < numberOfServers) {
            scheduleNextDeparture(currentTime, currentClient);
        } else {
            nextEvent = new Event(currentTime,EventType.QUEUEING,currentClient);
            EventSimulation.eventProcessor.processEvent(nextEvent); // process instantly
        }
    }

    public void processDeparture(Event departure) {
        currentTime = departure.getExecTime();
        currentClient = departure.getClient();
        double timeInSystem = currentTime - currentClient.getArrivalTime();
        double timeInQueue = currentClient.getTimeInQueue();
        currentClient.setTimeInSystem(timeInSystem);
        this.removeServer(currentClient);
        this.timesInSystem.add(timeInSystem);
        if (timeInQueue > 0.0) {
        } else {
            timeInQueue = 0.0;//to consider 0 queueing time in statistics
        }
        this.timesInQueue.add(timeInQueue);
        this.timesInService.add(timeInSystem - timeInQueue);

        this.amountsCharged.add(currentClient.getCar().getEnergyCharged());
        this.chargingDeviations.add(currentClient.getCar().getChargeDemand() - currentClient.getCar().getEnergyCharged());

        if (myQueue.getOccupation() > 0) {
            nextClient = myQueue.pullNextClientFromQueue(currentTime);
            if (nextClient.getTimeInQueue() <= 0.0) {
                logger.warning("Error: A Client with TimeInQueue = " + nextClient.getTimeInQueue() + " has been taken from the queue!");
            }
            scheduleNextDeparture(currentTime, nextClient);
        }

    }

    public void processQueueing(Event goInQueue) {
        currentTime = goInQueue.getExecTime();
        currentClient = goInQueue.getClient();
        if (myQueue.getOccupation() < this.getMyQueue().getSize()) {
            myQueue.addClientToQueue(currentClient);
        } else {
            nextEvent = new Event(currentTime,EventType.BLOCKING, currentClient);
            EventSimulation.eventProcessor.processEvent(nextEvent); // process instantly
        }
    }

    public void instantDeparture(Client currentClient) {
        // used to track down the negative time in system issue
        if (currentTime <= currentClient.getArrivalTime()) {
            System.out.println("current Time: " + currentTime
                    + " >? arrival Time: " + currentClient.getArrivalTime());
        }
        currentClient.setTimeInService(currentTime - (currentClient.getArrivalTime() + currentClient.getTimeInQueue()));
        nextEvent = new Event(EventSimulation.getCurrentTime(),EventType.DEPARTURE,currentClient);
        EventSimulation.eventProcessor.processEvent(nextEvent); // process instantly
    }

    public void scheduleNextDeparture(double currentTime, Client currentClient) {
        nextServer = getIdleServer(currentClient);
        if (nextServer == null) {
            logger.warning("Error: Cannot schedule next Departure - NO Server available: " + servers.size());
            return;
        }
        currentClient.getCar().setChargingPoint(this.getChargingSite().getChargingPoint(servers.indexOf(nextServer)));
        currentClient.getCar().setMyServer(nextServer);
        currentClient.getCar().updateChargingPower();
        double scale = getChargingSite().getMaxSitePower() / getChargingSite().getSitePower();
        if (scale < 1) {
            getChargingSite().scaleChargingPower(scale);
        }

        if (currentClient.getMeanServiceTime() > 0) {
            currentClient.setTimeInService(nextEvent.getExecTime() - currentTime);
            nextEvent = new Event(
                    currentTime + currentClient.getServiceTimeDistribution().getSample(currentClient.getMeanServiceTime()),
                    EventType.DEPARTURE,
                    currentClient
            );
        }
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public Server getServer(int index) {   // index works :)
        int n = servers.size();
        if (n > 0 && index < n) {
            return servers.get(index);
        } else {
            return (null);
        }
    }

    public void addServer(Server newServer) {
        servers.add(newServer);
        occupiedServers++;
    }

    public void removeServer() {
        servers.remove(0); //removes the first (longest occupied) server
        occupiedServers--;
    }

    public double getTotalPower() {
        return site.getSitePower();
    }
}