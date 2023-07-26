package queueingSystem;

import java.util.ArrayList;
import java.util.List;

public class Queue {
    private final int size;
    private int occupation = 0;

    private List<Client> queuedClients;

    public int getSize() {
        return size;
    }

    public Queue(int size) {
        this.queuedClients = new ArrayList<>(size);
        this.size = size;
    }

    public int getOccupation() {
        return occupation;
    }

    public void addClientToQueue(Client client) {
        occupation++;
        queuedClients.add(client);
    }

    public void removeClient(Client client) {
        occupation--;
        queuedClients.remove(client);
    }


}
