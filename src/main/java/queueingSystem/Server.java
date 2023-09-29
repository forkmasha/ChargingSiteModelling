package queueingSystem;

import chargingSite.ChargingPoint;
import chargingSite.Simulation;

import java.util.List;

public class Server {
    double serviceTime;
    ChargingPoint chargingPoint;
    private List<Double> serviceTimes;
    Client servedClient;

    public Server() {
        chargingPoint= new ChargingPoint(Simulation.MAX_POINT_POWER);
    }   // TO BE SHIFTED TO ChargingPoint and set via GUI

    public void setClient(Client client) {
        this.servedClient = client;
        this.chargingPoint.setChargedCar(client.getCar());

    }
    public Client getClient() {
       return this.servedClient;
    }
}