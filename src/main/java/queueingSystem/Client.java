package queueingSystem;

import chargingSite.ElectricVehicle;
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
    public Client(double arrivalTime, ElectricVehicle car, QueueingSystem system) {  //change meanServiceTime to CarType
        this.myIndex = clientCounter++;
        this.arrivalTime = arrivalTime;
        this.meanServiceTime = car.getMeanServiceTime();
        this.system = system;
        this.addServiceTimeDistribution(car.getSimParameters().getServiceType());
        this.car = car; // change to new EV(CarType)
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

    public void processClient(double deltaTime) {
        if (car==null) {System.out.println("ERROR in processClient: Client without Car!");}
        this.car.addEnergyCharged(deltaTime);
        this.car.updateChargingPower();
        this.getSystem().getSitePowers().add(this.getSystem().getChargingSite().getSitePower());
    }
}