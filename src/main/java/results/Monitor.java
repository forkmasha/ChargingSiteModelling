package results;

import queueingSystem.QueueingSystem;

import java.util.ArrayList;
import java.util.List;

public class Monitor {
    private QueueingSystem source;
    private String name;
    public List<Double> values = new ArrayList<>();


    public void setSource(QueueingSystem source) {
        this.source = source;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Monitor() {

    }

}