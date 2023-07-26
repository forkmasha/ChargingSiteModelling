package queueingSystem;

import java.util.List;

public class Server {
    double serviceTime;
    private List<Double> serviceTimes;
    Client servedClient;

    public Server() {
    }

    public void setClient(Client client) {
        this.servedClient = client;
    }
}
