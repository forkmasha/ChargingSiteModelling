package queueingSystem;

import chargingSite.ChargingPoint;
import chargingSite.Simulation;

import java.util.List;

public class Server {
    double serviceTime;
    ChargingPoint chargingPoint;
    private List<Double> serviceTimes;
    Client servedClient;

    public Server(QueueingSystem mySystem, Client myClient) {
        this.chargingPoint = mySystem.getChargingSite().getIdleChargingPoint(myClient.getCar().getPlugType());
        if (this.chargingPoint != null) {
            this.setClient(myClient);
        } else {
            System.out.println("ERROR: Server without ChargePoint created!");
        }
    }

    public void setClient(Client client) {
        this.servedClient = client;
        this.chargingPoint.pluginCar(client.getCar());
        if (chargingPoint.getChargedCar() == null) {
            System.out.println("ERROR in Server setClient: Could not assign a car to the linked chargingPoint!");
        }
    }
    public Client getClient() {
       return this.servedClient;
    }
}