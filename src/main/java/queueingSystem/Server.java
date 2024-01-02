package queueingSystem;

import chargingSite.ChargingPoint;
import chargingSite.Simulation;
import exceptions.ServerException;

import java.util.List;

public class Server {
    double serviceTime;
    ChargingPoint chargingPoint;
    private List<Double> serviceTimes;
    Client servedClient;

    public Server(QueueingSystem mySystem, Client myClient){
        this.chargingPoint = mySystem.getChargingSite().getIdleChargingPoint(myClient.getCar().getPlugType());
        if (this.chargingPoint != null) {
            this.setClient(myClient);
        } else {
            throw new ServerException("Server without ChargePoint created!");
        }
    }

    public void setClient(Client client) {
        this.servedClient = client;
        this.chargingPoint.pluginCar(client.getCar());
        if (chargingPoint.getChargedCar() == null) {
            throw new ServerException("Could not assign a car to the linked chargingPoint!");
        }
    }
    public Client getClient() {
       return this.servedClient;
    }
}