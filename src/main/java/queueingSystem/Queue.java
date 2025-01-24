package queueingSystem;

import distributions.UniformDistribution;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class Queue {
    private final int size;
    private final QueueingType type;
    private List<Client> queuedClients;
    private int occupation = 0;
    private static final Logger logger = Logger.getLogger(Queue.class.getName());

    public Queue(int size, QueueingType type) {
        this.size = size;
        this.type = type;
        this.queuedClients = new ArrayList<>(size);
    }
    public int getSize() {
        return size;
    }
    public int getOccupation() {
        return occupation;
    }

    public void addClientToQueue(Client client) {
        queuedClients.add(client);
        occupation++;
        if(occupation>size){
            logger.warning("Error: There are more clients in the queue than it is configured to host!");
        }
        if(queuedClients.size()>size){
            logger.warning("Error: Queue size exceeded! " + queuedClients.size() + " > " + size);
        }
    }
    public Client getNextClient() {
        queuedClients.sort(Comparator.comparingDouble(Client::getArrivalTime));
        switch (this.type) {
            case FIFO -> { return queuedClients.get(0); }
            case LIFO -> { return queuedClients.get(queuedClients.size()-1); }
            case RAND -> {
                int i = (int) Math.floor(UniformDistribution.createSample(0.5 * queuedClients.size()));
                return queuedClients.get(i);
            }
            default -> { return null; }
        }
    }
    public Client pullNextClientFromQueue(double currentTime) {
        if( queuedClients.size() != occupation ){
            logger.warning("Error: Queue size mismatch! " + queuedClients.size() + " > " + occupation);
        }
        else if ( occupation == 0 ) {
            logger.warning("Error: Cannot pull a Client from empty Queue!");
        }

        Client nextClient = getNextClient();
        nextClient.setTimeInQueue(currentTime - nextClient.getArrivalTime());
        queuedClients.remove(nextClient);
        occupation--;

        if( occupation < 0 ){
            logger.warning("Error: There are less than zero clients left in the queue!");
            return null;
        }
        return nextClient;
    }
}