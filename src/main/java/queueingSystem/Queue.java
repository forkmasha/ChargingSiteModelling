package queueingSystem;
import java.util.List;

public class Queue {
    private final int size;
    private int occupation = 0;

    private List<Client> queuedClients;


    public Queue(int size) {
        this.size = size;
    }

    public int getOccupation() {
        return occupation;
    }

    public void addClientToQueue(Client client){
        occupation++;
        queuedClients.add(client);
    }

    public void removeClient(Client client){
        occupation--;
        queuedClients.remove(client);
    }


}
