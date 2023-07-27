package queueingSystem;

import distributions.Distribution;
import distributions.DistributionType;

public class Client {
    private double meanServiceTime;
    private double timeInSystem;
    private double timeInQueue;
    private double arrivalTime;
    private QueueingSystem system;
    private Distribution serviceTimeDistribution;

    public QueueingSystem getSystem() {
        return system;
    }

    public void setTimeInSystem(double time) {
        this.timeInSystem = time;
    }

    public void setTimeInQueue(double time) {
        this.timeInQueue = time;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setSystem(QueueingSystem system) {
        this.system = system;
    }

    public void addServiceTimeDistribution(DistributionType type) {
        this.serviceTimeDistribution = Distribution.create(type);
    }

    public void setMeanServiceTime(double meanServiceTime) {
        this.meanServiceTime = meanServiceTime;
    }

    public double getTimeInSystem() {
        return timeInSystem;
    }

    public double getTimeInQueue() {
        return timeInQueue;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public Client(double meanServiceTime, DistributionType type, QueueingSystem system) {
        this.meanServiceTime = meanServiceTime;
        this.system = system;
        this.addServiceTimeDistribution(type);
    }

    public double getMeanServiceTime() {
        return meanServiceTime;
    }

    public Distribution getServiceTimeDistribution() {
        return serviceTimeDistribution;
    }
}
