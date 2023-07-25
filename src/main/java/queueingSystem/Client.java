package queueingSystem;

import distributions.Distribution;
import distributions.DistributionType;

public class Client {
    private double meanServiceTime;
    private QueueingSystem system;
    private Distribution serviceTimeDistribution;

    public QueueingSystem getSystem() {
        return system;
    }

    public void setSystem(QueueingSystem system) {
        this.system = system;
    }

    public void setServiceTimeDistribution(DistributionType type) {
        this.serviceTimeDistribution = Distribution.create(type);
    }

    public void setMeanServiceTime(double meanServiceTime) {
        this.meanServiceTime = meanServiceTime;
    }


    public Client(double meanServiceTime) {
        this.meanServiceTime = meanServiceTime;
    }

    public double getMeanServiceTime() {
        return meanServiceTime;
    }

    public Distribution getServiceTimeDistribution() {
        return serviceTimeDistribution;
    }
}
