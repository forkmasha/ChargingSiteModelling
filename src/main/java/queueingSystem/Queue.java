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
        //System.out.print("+");
        queuedClients.add(client);
        occupation++;
        if(occupation>size){
            System.out.println("Error: There are more clients in the queue than it is configured to host!");
        }
        if(queuedClients.size()>size){
            System.out.println("Error: Queue size exceeded! " + queuedClients.size() + " > " + size);
        }
    }

    public void removeClient(Client client) {
        //System.out.print("-");
        queuedClients.remove(0);
        //queuedClients.remove(client); // somehow that does not do it...
        occupation--;
        if(occupation<0){
            System.out.println("Error: There are less than zero clients in the queue!");
        //} else if (occupation==0) { System.out.println("Queue is empty again :-)");
        }
        if(queuedClients.size()!=occupation){
            System.out.println("Error: Queue size mismatch! " + queuedClients.size() + " > " + occupation);
        }
    }
}
