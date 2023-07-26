package results;

import java.util.List;

public class Times {
    private List<Double> means;
    private List<Double> stds;
    private List<Double> confidences;

    private Statistics functions = new Statistics();
    public void addMeanServiceTime(List<Double> values){
        means.add(functions.getMean(values));
    }

    public void addMeanQueueTime(List<Double> values){
        stds.add(functions.getStd(values));
    }

    public void addMeanSystemTime(List<Double> values){
        confidences.add(functions.getConfidenceInterval(values,95));
    }



}
