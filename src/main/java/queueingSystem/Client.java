package queueingSystem;

import chargingSite.ElectricVehicle;
import chargingSite.Simulation;
import distributions.Distribution;
import distributions.DistributionType;

public class Client {
    private static int clientCounter = 0;
    public static int getClientCounter() { return clientCounter; }
    public static void resetClientCounter() { clientCounter = 0; }
    private final int myIndex;
    private final double meanServiceTime;
    private final double arrivalTime;
    private double timeInService = 0.0;
    private double timeInSystem = 0.0;
    private double timeInQueue = 0.0;
    private QueueingSystem system;
    private Distribution serviceTimeDistribution;
    private ElectricVehicle car;
    public ElectricVehicle getCar() {
        return car;
    }
    // public void setCar(ElectricVehicle car) {
    //this.car = car;
    //}

    public Client(double arrivalTime, double meanServiceTime, DistributionType serviceType, QueueingSystem system) {
        this.myIndex = clientCounter++;
        this.arrivalTime = arrivalTime;
        this.meanServiceTime = meanServiceTime;
        this.system = system;
        this.addServiceTimeDistribution(serviceType);
        this.car=new ElectricVehicle("car", Simulation.MAX_EV_POWER,60, DistributionType.BETA);  // batteryCapacity may also be set in GUI some time...
        this.car.setQueueingSystem(system);
        this.car.setMeanServiceTime(meanServiceTime);
    }
    public int getMyIndex() { return myIndex; }
    public QueueingSystem getSystem() {
        return system;
    }

    public void setTimeInService(double time) {
        this.timeInService = time;
    }
    public void setTimeInSystem(double time) {
        this.timeInSystem = time;
    }
    public void setTimeInQueue(double time) {
        this.timeInQueue = time;
    }

    public void addServiceTimeDistribution(DistributionType type) {
        this.serviceTimeDistribution = Distribution.create(type);
    }
    public double getMeanServiceTime() {
        return meanServiceTime;
    }
    public Distribution getServiceTimeDistribution() {
        return serviceTimeDistribution;
    }

    public double getTimeInQueue() {
        return timeInQueue;
    }
    public double getArrivalTime() {
        return arrivalTime;
    }

    public void processClient(double deltaTime, double sitePower) {
        this.car.addEnergyCharged(deltaTime,sitePower);
        this.getSystem().getSitePowers().add(sitePower);
    }

}