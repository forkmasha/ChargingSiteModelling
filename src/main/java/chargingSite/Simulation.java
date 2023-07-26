package chargingSite;

import distributions.Distribution;
import distributions.DistributionType;
import queueingSystem.QueueingSystem;

import java.util.Random;

import static java.lang.Math.log;

public class Simulation {
    private int numServers;
    private int queueLength;
    private int numStates;
    private int maxCars;
    private double meanArrivalInterval;
    private double meanServiceTime;

    private Distribution arrivalDistribution;
    private Distribution serviceDistribution;
    private Distribution demandDistribution;


    public Simulation(int numServers, int queueLength, int numStates, int maxCars,
                                double meanArrivalInterval, double meanServiceTime) {
        this.numServers = numServers;
        this.queueLength = queueLength;
        this.numStates = numStates;
        this.maxCars = maxCars;
        this.meanArrivalInterval = meanArrivalInterval;
        this.meanServiceTime = meanServiceTime;

        //this.distributionType=distributionType;
        System.out.println("\n#########################");
        System.out.println("Simulation Parameters");
        System.out.println("#########################");
        System.out.println("MeanInterArrivalTime " + meanArrivalInterval);
        System.out.println("MeanServiceTime " + meanServiceTime);
        System.out.println("Number of servers " + numServers);
        System.out.println("Queue length " + queueLength);

        /*k = 0;
        j = 0;
        i = 0;
        time = 0.0;
        previousTime = 0.0;

        servicedCars.clear();
        queueStartTimes.clear();

        systemTimeList.clear();
        queueTimeList.clear();

        systemTimes.clear();
        queueTimes.clear();
        serviceTimes.clear();

        arrivalRateList.clear();

        eventStack.events.clear();
        eventStack.addEvent(new ArrivalEvent(0.0));   // insert initial arrival
*/

        // Головний цикл моделювання
 /*       while (!eventStack.isEmpty()) {
            // Отримуємо наступну подію зі стеку
            Event event = eventStack.getNextEvent();
            double eventTime = event.getTime();
            // Оновлюємо час моделювання та розраховуємо різницю з попереднім часом
            time = eventTime;
            double deltaTime = time - previousTime;

            // Оновлюємо часи перебування в системі та черзі для автомобілів
            updateCarTimes(deltaTime);
            // Обробляємо подію залежно від її типу
            if (event instanceof ArrivalEvent) {
                processArrivalEvent(eventTime);
            } else if (event instanceof DepartureEvent) {
                processDepartureEvent(eventTime);
            }
            // Оновлюємо попередній час
            previousTime = time;
        }
        // Генеруємо статистику симуляції
        generateStatistics();
        //  generateServiceTimeHistogram();
    }
*/

}

    public void runSimulation() {
        //QueueingSystem gasStation = new QueueingSystem(numServers, queueLength, numStates, maxCars, meanArrivalInterval,arrivalDistribution, meanServiceTime,serviceDistribution);
        //gasStation.simulate();
    }
}
