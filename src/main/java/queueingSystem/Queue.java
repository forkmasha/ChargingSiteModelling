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
        System.out.print("+");
        if(occupation>size){
            System.out.println("Error: There are more clients in the queue than it is configured to host!");
        }
        queuedClients.add(client);
    }

    public void removeClient(Client client) {
        occupation--;
        if(occupation<0){
            System.out.println("Error: There are less than zero clients in the queue!");
        }
        System.out.print("-");
        queuedClients.remove(client);
    }
}
