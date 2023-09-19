package queueingSystem;

import chargingSite.ChargingPoint;

import java.util.List;

public class Server {
    double serviceTime;
    ChargingPoint chargingPoint;
    private List<Double> serviceTimes;
    Client servedClient;

    public Server() {
        chargingPoint= new ChargingPoint(50);
    }

    public void setClient(Client client) {
        this.servedClient = client;
        this.chargingPoint.setChargedCar(client.getCar());

    }

}